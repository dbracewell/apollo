package com.davidbracewell.apollo.optimization.loss;

import org.apache.commons.math3.util.FastMath;

import java.io.Serializable;

/**
 * @author David B. Bracewell
 */
public class CrossEntropyLoss implements LossFunction, Serializable {
   private static final long serialVersionUID = 1L;

   @Override
   public double derivative(double predictedValue, double trueValue) {
      return predictedValue - trueValue;
   }

   @Override
   public double loss(double predictedValue, double trueValue) {
      return trueValue * FastMath.log(predictedValue);
   }

}// END OF CrossEntropyLoss
