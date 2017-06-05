package com.davidbracewell.apollo.optimization.activation;

import com.davidbracewell.apollo.linalg.Vector;

/**
 * @author David B. Bracewell
 */
public class SoftmaxActivation implements Activation {
   private static final long serialVersionUID = 1L;

   @Override
   public double apply(double x) {
      return SigmoidActivation.INSTANCE.apply(x);
   }

   @Override
   public Vector apply(Vector x) {
      double max = x.max();
      x.mapSelf(d -> Math.exp(d - max));
      double sum = x.sum();
      return x.mapDivideSelf(sum);
   }

   @Override
   public boolean isProbabilistic() {
      return true;
   }
}// END OF SoftmaxActivation