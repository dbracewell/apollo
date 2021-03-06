package com.davidbracewell.apollo.ml.regression;

import com.davidbracewell.apollo.linear.NDArray;
import com.davidbracewell.collection.counter.Counter;
import com.davidbracewell.collection.counter.Counters;
import lombok.NonNull;

/**
 * Simple regression model implementation
 *
 * @author David B. Bracewell
 */
public class SimpleRegressionModel extends Regression {
   private static final long serialVersionUID = 1L;
   /**
    * The Weights.
    */
   NDArray weights;
   /**
    * The Bias.
    */
   double bias;

   public SimpleRegressionModel(RegressionLearner learner) {
      super(learner);
   }


   @Override
   public double estimate(@NonNull NDArray vector) {
      return bias + weights.dot(vector);
   }

   @Override
   public Counter<String> getFeatureWeights() {
      Counter<String> out = Counters.newCounter();
      out.set("***BIAS***", bias);
      weights.forEachSparse(e -> out.set(decodeFeature(e.getIndex()).toString(), e.getValue()));
      return out;
   }

}// END OF SimpleRegressionModel
