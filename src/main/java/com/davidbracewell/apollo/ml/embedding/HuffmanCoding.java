package com.davidbracewell.apollo.ml.embedding;

import com.davidbracewell.collection.Collect;
import com.davidbracewell.collection.Counter;
import com.davidbracewell.collection.Counters;
import com.davidbracewell.collection.Index;
import com.davidbracewell.collection.Indexes;
import com.google.common.collect.ImmutableMap;
import lombok.NonNull;
import lombok.Value;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author David B. Bracewell
 */
public class HuffmanCoding implements Serializable {

  private final Counter<String> vocabulary;
  private final Index<String> index;

  public HuffmanCoding(@NonNull Counter<String> vocabulary) {
    this.vocabulary = vocabulary;
    this.index = Indexes.newIndex(vocabulary.itemsByCount(false));
  }

  public static void main(String[] args) {
    Counter<String> c = Counters.newHashMapCounter(
      Collect.map(
        "the", 4000,
        "ship", 500,
        "gold", 2,
        "silver", 40
      )
    );

    HuffmanCoding coding = new HuffmanCoding(c);
    coding.encode().forEach((k, v) -> System.out.println(k + " : " + v));


  }

  public Map<String, HuffmanNode> encode() {
    final int nTokens = vocabulary.size();
    int[] parentNode = new int[nTokens * 2 + 1];
    byte[] binary = new byte[nTokens * 2 + 1];
    long[] count = new long[nTokens * 2 + 1];

    AtomicInteger i = new AtomicInteger();
    index.forEach(k ->
      count[i.getAndIncrement()] = (long) vocabulary.get(k)
    );

    for (int j = nTokens; j < count.length; j++) {
      count[j] = (long) 1e15;
    }

    buildTree(nTokens, count, binary, parentNode);
    return encode(binary, parentNode);
  }


  private Map<String, HuffmanNode> encode(byte[] binary, int[] parentNode) {
    int numTokens = vocabulary.size();

    // Now assign binary code to each unique token
    ImmutableMap.Builder<String, HuffmanNode> result = ImmutableMap.builder();
    int nodeIdx = 0;
    for (Map.Entry<String, Double> e : vocabulary.entries()) {
      int curNodeIdx = index.indexOf(e.getKey());
      ArrayList<Byte> code = new ArrayList<>();
      ArrayList<Integer> points = new ArrayList<>();
      while (true) {
        code.add(binary[curNodeIdx]);
        points.add(curNodeIdx);
        curNodeIdx = parentNode[curNodeIdx];
        if (curNodeIdx == numTokens * 2 - 2)
          break;
      }
      int codeLen = code.size();
      final int count = e.getValue().intValue();
      final byte[] rawCode = new byte[codeLen];
      final int[] rawPoints = new int[codeLen + 1];

      rawPoints[0] = numTokens - 2;
      for (int i = 0; i < codeLen; i++) {
        rawCode[codeLen - i - 1] = code.get(i);
        rawPoints[codeLen - i] = points.get(i) - numTokens;
      }

      String token = e.getKey();
      result.put(token, new HuffmanNode(rawCode, rawPoints, nodeIdx, count));

      nodeIdx++;
    }

    return result.build();
  }

  private void buildTree(int nTokens, long[] count, byte[] binary, int[] parentNode) {
    int min1i;
    int min2i;
    int pos1 = nTokens - 1;
    int pos2 = nTokens;


    for (int a = 0; a < nTokens - 1; a++) {

      if (pos1 >= 0) {
        if (count[pos1] < count[pos2]) {
          min1i = pos1;
          pos1--;
        } else {
          min1i = pos2;
          pos2++;
        }
      } else {
        min1i = pos2;
        pos2++;
      }

      if (pos1 >= 0) {
        if (count[pos1] < count[pos2]) {
          min2i = pos1;
          pos1--;
        } else {
          min2i = pos2;
          pos2++;
        }
      } else {
        min2i = pos2;
        pos2++;
      }


      int newNodeIndex = nTokens + a;
      count[newNodeIndex] = count[min1i] + count[min2i];
      parentNode[min1i] = newNodeIndex;
      parentNode[min2i] = newNodeIndex;
      binary[min2i] = 1;


    }

  }


  @Value
  public static class HuffmanNode implements Serializable {
    byte[] code;
    int[] point;
    int idx;
    long count;
  }

}// END OF HuffmanCoding