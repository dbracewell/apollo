package com.davidbracewell.apollo.optimization.activation;

/**
 * @author David B. Bracewell
 */
public class SigmoidFunction implements Activation {
   private static final long serialVersionUID = 1L;
   public static final SigmoidFunction INSTANCE = new SigmoidFunction();

   @Override
   public double apply(double x) {
      if (x > 0) {
         return 1.0 / (1.0 + Math.exp(-x));
      }
      double z = Math.exp(x);
      return z / (1 + z);
   }

   @Override
   public boolean isProbabilistic() {
      return true;
   }
}// END OF SigmoidFunction
