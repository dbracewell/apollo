package com.davidbracewell.apollo.ml.classification.nn.slt;

import com.davidbracewell.apollo.linalg.DenseFloatMatrix;
import com.davidbracewell.apollo.linalg.Matrix;
import com.davidbracewell.tuple.Tuple2;
import lombok.Getter;
import lombok.val;

import static com.davidbracewell.tuple.Tuples.$;

/**
 * @author David B. Bracewell
 */
public class Dropout extends Layer {
   double rate;

   protected Dropout(int inputSize, int outputSize, double rate) {
      super(inputSize, outputSize);
      this.rate = rate;
   }

   public static DropoutBuilder builder() {
      return new DropoutBuilder();
   }

   public static DropoutBuilder builder(double rate) {
      return new DropoutBuilder().rate(rate);
   }

   @Override
   public Matrix backward(Matrix input, Matrix output, Matrix delta, double learningRate, int layerIndex, int iteration) {
      return delta;
   }

   @Override
   public BackpropResult backward(Matrix input, Matrix output, Matrix delta, boolean calculateDelta) {
      return BackpropResult.from(delta, DenseFloatMatrix.empty(), DenseFloatMatrix.empty());
   }

   @Override
   public Tuple2<Matrix, Double> backward(WeightUpdate updater, Matrix input, Matrix output, Matrix delta, int iteration, boolean calcuateDelta) {
      return $(delta, 0d);
   }

   @Override
   public Layer copy() {
      return new Dropout(getInputSize(), getOutputSize(), rate);
   }

   @Override
   Matrix forward(Matrix input) {
      val mask = DenseFloatMatrix.rand(input.numRows(), input.numCols())
                                 .predicate(x -> x > rate)
                                 .mapi(x -> x / (1.0 - rate));
      return input.muli(mask);
   }

   @Override
   public boolean trainOnly() {
      return true;
   }

   @Override
   public double update(WeightUpdate weightUpdate, Matrix wGrad, Matrix bBrad, int iteration) {
      return 0;
   }

   public static class DropoutBuilder extends LayerBuilder<DropoutBuilder> {
      @Getter
      double rate = 0.5;

      @Override
      public Layer build() {
         return new Dropout(getInputSize(), getOutputSize(), getRate());
      }

      public DropoutBuilder rate(double rate) {
         this.rate = rate;
         return this;
      }
   }

   @Override
   public Matrix getWeights() {
      return DenseFloatMatrix.empty();
   }

   @Override
   public Matrix getBias() {
      return DenseFloatMatrix.empty();
   }

   @Override
   public void update(Matrix[] weights, Matrix[] bias) {

   }
}// END OF Dropout
