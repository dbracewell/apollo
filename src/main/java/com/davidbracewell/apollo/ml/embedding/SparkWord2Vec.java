package com.davidbracewell.apollo.ml.embedding;

import com.davidbracewell.apollo.linear.NDArrayFactory;
import com.davidbracewell.apollo.linear.store.DefaultVectorStore;
import com.davidbracewell.apollo.linear.store.LSHVectorStore;
import com.davidbracewell.apollo.linear.store.VectorStoreBuilder;
import com.davidbracewell.apollo.ml.Instance;
import com.davidbracewell.apollo.ml.data.Dataset;
import com.davidbracewell.apollo.ml.encoder.Encoder;
import com.davidbracewell.apollo.ml.encoder.IndexEncoder;
import com.davidbracewell.apollo.ml.sequence.Sequence;
import com.davidbracewell.apollo.stat.measure.Similarity;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.guava.common.base.Throwables;
import com.davidbracewell.stream.SparkStream;
import lombok.Getter;
import lombok.Setter;
import org.apache.spark.mllib.feature.Word2Vec;
import org.apache.spark.mllib.feature.Word2VecModel;
import scala.collection.JavaConversions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>Wrapper around Spark's Word2Vec implementation</p>
 *
 * @author David B. Bracewell
 */
public class SparkWord2Vec extends EmbeddingLearner {
   private static final long serialVersionUID = 1L;
   @Getter
   @Setter
   private int minCount = 5;
   @Getter
   @Setter
   private int numIterations = 1;
   @Getter
   @Setter
   private double learningRate = 0.025;
   @Getter
   @Setter
   private long randomSeed = new Date().getTime();
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
      Word2Vec w2v = new Word2Vec();
      w2v.setMinCount(minCount);
      w2v.setVectorSize(getDimension());
      w2v.setLearningRate(learningRate);
      w2v.setNumIterations(numIterations);
      w2v.setWindowSize(getWindowSize());
      w2v.setSeed(randomSeed);
      SparkStream<Iterable<String>> sentences = new SparkStream<>(dataset.stream()
                                                                         .map(sequence -> {
                                                                            List<String> sentence = new ArrayList<>();
                                                                            for (Instance instance : sequence) {
                                                                               sentence.add(instance.getFeatures()
                                                                                                    .get(0)
                                                                                                    .getFeatureName());
                                                                            }
                                                                            return sentence;
                                                                         }));
      Word2VecModel model = w2v.fit(sentences.getRDD());
      Encoder encoder = new IndexEncoder();
      VectorStoreBuilder<String> builder;
      if (fastKNN) {
         builder = LSHVectorStore.<String>builder().signature("COSINE");
      } else {
         builder = DefaultVectorStore.builder();
      }
      builder.dimension(getDimension());
      builder.measure(Similarity.Cosine);
      for (Map.Entry<String, float[]> vector : JavaConversions.mapAsJavaMap(model.getVectors()).entrySet()) {
         encoder.encode(vector.getKey());
         builder.add(vector.getKey(), NDArrayFactory.wrap(Convert.convert(vector.getValue(), double[].class)));
      }
      try {
         return new Embedding(builder.build());
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }


}// END OF SparkWord2Vec
