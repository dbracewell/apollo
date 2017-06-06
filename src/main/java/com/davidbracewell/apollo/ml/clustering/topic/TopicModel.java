package com.davidbracewell.apollo.ml.clustering.topic;

import com.davidbracewell.apollo.affinity.DistanceMeasure;
import com.davidbracewell.apollo.linalg.Vector;
import com.davidbracewell.apollo.ml.EncoderPair;
import com.davidbracewell.apollo.ml.Instance;
import com.davidbracewell.apollo.ml.clustering.flat.FlatClustering;
import com.davidbracewell.apollo.optimization.Optimum;
import com.davidbracewell.collection.counter.Counter;
import lombok.NonNull;

/**
 * @author David B. Bracewell
 */
public abstract class TopicModel extends FlatClustering {
   private static final long serialVersionUID = 1L;
   protected int K;

   @Override
   public int size() {
      return K;
   }

   protected TopicModel(EncoderPair encoderPair, DistanceMeasure distanceMeasure) {
      super(encoderPair, distanceMeasure);
   }

   @Override
   public int hardCluster(@NonNull Instance instance) {
      return Optimum.MAXIMUM.optimumIndex(softCluster(instance));
   }

   /**
    * Gets topic vector.
    *
    * @param topic the topic
    * @return the topic vector
    */
   public abstract Vector getTopicVector(int topic);

   /**
    * Gets the words and their probabilities for a given topic
    *
    * @param topic the topic
    * @return the topic words
    */
   public abstract Counter<String> getTopicWords(int topic);

   /**
    * Gets the distribution across topics for a given feature.
    *
    * @param feature the feature (word) whose topic distribution is desired
    * @return the distribution across topics for the given feature
    */
   public abstract double[] getTopicDistribution(String feature);

}// END OF TopicModel
