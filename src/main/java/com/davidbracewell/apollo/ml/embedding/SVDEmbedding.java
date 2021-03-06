/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.davidbracewell.apollo.ml.embedding;

import com.davidbracewell.Math2;
import com.davidbracewell.apollo.linear.Axis;
import com.davidbracewell.apollo.linear.NDArray;
import com.davidbracewell.apollo.linear.NDArrayFactory;
import com.davidbracewell.apollo.linear.store.DefaultVectorStore;
import com.davidbracewell.apollo.linear.store.LSHVectorStore;
import com.davidbracewell.apollo.linear.store.VectorStoreBuilder;
import com.davidbracewell.apollo.ml.Instance;
import com.davidbracewell.apollo.ml.data.Dataset;
import com.davidbracewell.apollo.ml.encoder.Encoder;
import com.davidbracewell.apollo.ml.encoder.IndexEncoder;
import com.davidbracewell.apollo.ml.sequence.Sequence;
import com.davidbracewell.apollo.stat.measure.Association;
import com.davidbracewell.apollo.stat.measure.ContingencyTable;
import com.davidbracewell.apollo.stat.measure.ContingencyTableCalculator;
import com.davidbracewell.apollo.stat.measure.Similarity;
import com.davidbracewell.collection.counter.MultiCounter;
import com.davidbracewell.guava.common.base.Throwables;
import com.davidbracewell.stream.StreamingContext;
import com.davidbracewell.stream.accumulator.MMultiCounterAccumulator;
import lombok.Getter;
import lombok.Setter;
import org.apache.spark.mllib.linalg.DenseVector;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.linalg.SingularValueDecomposition;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;

import java.io.IOException;
import java.util.Map;

import static com.davidbracewell.apollo.linear.SparkLinearAlgebra.*;
import static com.davidbracewell.tuple.Tuples.$;

/**
 * @author David B. Bracewell
 */
public class SVDEmbedding extends EmbeddingLearner {
   private static final long serialVersionUID = 1L;
   @Getter
   @Setter
   private ContingencyTableCalculator calculator = Association.PPMI;
   @Getter
   @Setter
   private int windowSize = 5;
   @Getter
   @Setter
   private boolean fastKNN = false;

   @Override
   public void resetLearnerParameters() {

   }

   @Override
   protected Embedding trainImpl(Dataset<Sequence> dataset) {

      final Map<String, Double> unigrams = dataset
                                              .stream()
                                              .flatMap(sequence -> sequence
                                                                      .asInstances()
                                                                      .stream()
                                                                      .flatMap(Instance::getFeatureSpace)
                                                      )
                                              .mapToPair(s -> $(s, 1.0))
                                              .reduceByKey(Math2::add)
                                              .collectAsMap();

      final Encoder featureEncoder = new IndexEncoder();
      featureEncoder.fit(StreamingContext.local().stream(unigrams.keySet()));

      MMultiCounterAccumulator<Integer, Integer> accumulator = dataset.getStreamingContext().multiCounterAccumulator();
      dataset.stream()
             .forEach(sequence -> {
                for (int i = 0; i < sequence.size(); i++) {
                   if (sequence.get(i).getFeatures().size() > 0) {
                      int iFeature = (int) featureEncoder.encode(sequence.get(i).getFeatures().get(0).getFeatureName());
                      for (int j = Math.min(i - getWindowSize(), 0); j <= Math.max(sequence.size() - 1,
                                                                                   i + getWindowSize()); j++) {
                         if (sequence.get(j).getFeatures().size() > 0) {
                            int jFeature = (int) featureEncoder.encode(sequence.get(j)
                                                                               .getFeatures()
                                                                               .get(0)
                                                                               .getFeatureName());
                            accumulator.increment(Math.min(iFeature, jFeature), Math.max(iFeature, jFeature));
                         }
                      }
                   }
                }
             });


      MultiCounter<Integer, Integer> windowCounts = accumulator.value();
      final double totalCounts = unigrams.values().parallelStream().count();
      RowMatrix mat = new RowMatrix(StreamingContext.distributed().range(0, featureEncoder.size())
                                                    .map(i -> {
                                                       double[] v = new double[featureEncoder.size()];
                                                       double iCount = unigrams.get(
                                                          featureEncoder.decode(i).toString());
                                                       for (int j = 0; j < featureEncoder.size(); j++) {
                                                          double jCount = unigrams.get(
                                                             featureEncoder.decode(j).toString());
                                                          double n11 = Math.max(windowCounts.get(i, j),
                                                                                windowCounts.get(j, i));

                                                          v[j] = calculator.calculate(
                                                             ContingencyTable.create2X2(n11, iCount, jCount,
                                                                                        totalCounts));
                                                       }
                                                       return (Vector) new DenseVector(v);
                                                    })
                                                    .getRDD()
                                                    .cache()
                                                    .rdd());


      VectorStoreBuilder<String> builder;
      if (fastKNN) {
         builder = LSHVectorStore.<String>builder().signature("COSINE");
      } else {
         builder = DefaultVectorStore.builder();
      }
      builder.dimension(getDimension());
      builder.measure(Similarity.Cosine);

      SingularValueDecomposition<RowMatrix, Matrix> svd = sparkSVD(mat, getDimension());
      NDArray em = toMatrix(svd.U()).mmul(toDiagonalMatrix(svd.s()));
      for (int i = 0; i < em.numRows(); i++) {
         builder.add(featureEncoder.decode(i).toString(), NDArrayFactory.wrap(em.getVector(i, Axis.ROW).toArray()));
      }
      try {
         return new Embedding(builder.build());
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

}//END OF SparkLSA
