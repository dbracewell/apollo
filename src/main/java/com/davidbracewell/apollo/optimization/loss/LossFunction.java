package com.davidbracewell.apollo.optimization.loss;

import com.davidbracewell.apollo.linalg.Matrix;
import com.davidbracewell.apollo.linalg.Vector;
import com.davidbracewell.apollo.optimization.CostGradientTuple;
import com.davidbracewell.apollo.optimization.Gradient;
import com.davidbracewell.guava.common.base.Preconditions;
import lombok.NonNull;

/**
 * The interface Loss function.
 *
 * @author David B. Bracewell
 */
public interface LossFunction {

   /**
    * Derivative double.
    *
    * @param predictedValue the predicted value
    * @param trueValue      the true value
    * @return the double
    */
   double derivative(double predictedValue, double trueValue);

   /**
    * Derivative double.
    *
    * @param predictedValue the predicted value
    * @param trueValue      the true value
    * @return the double
    */
   default Gradient derivative(@NonNull Vector predictedValue, @NonNull Vector trueValue) {
      Vector v = predictedValue.map(trueValue, this::derivative);
      return Gradient.of(v.transpose(), v);
   }

   /**
    * Loss double.
    *
    * @param predictedValue the predicted value
    * @param trueValue      the true value
    * @return the double
    */
   default double loss(@NonNull Vector predictedValue, @NonNull Vector trueValue) {
      return predictedValue.map(trueValue, this::loss).sum();
   }

   default double loss(@NonNull Matrix predicted, @NonNull Matrix trueValue) {
      Preconditions.checkArgument(predicted.shape().equals(trueValue.shape()), "Dimension mismatch");
      double totalLoss = 0d;
      for (int r = 0; r < predicted.numberOfRows(); r++) {
         totalLoss += loss(predicted.row(r), trueValue.row(r));
      }
      return totalLoss;
   }

   /**
    * Loss double.
    *
    * @param predictedValue the predicted value
    * @param trueValue      the true value
    * @return the double
    */
   double loss(double predictedValue, double trueValue);

   /**
    * Loss and derivative loss valueGradient tuple.
    *
    * @param predictedValue the predicted value
    * @param trueValue      the true value
    * @return the loss valueGradient tuple
    */
   default CostGradientTuple lossAndDerivative(@NonNull Vector predictedValue, @NonNull Vector trueValue) {
      return CostGradientTuple.of(loss(predictedValue, trueValue), derivative(predictedValue, trueValue));
   }

}//END OF LossFunction
