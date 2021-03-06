package com.davidbracewell.apollo.ml.classification.nn;

import com.davidbracewell.apollo.linear.Axis;
import com.davidbracewell.apollo.linear.NDArray;
import com.davidbracewell.apollo.linear.NDArrayInitializer;
import com.davidbracewell.apollo.ml.optimization.WeightUpdate;
import com.davidbracewell.apollo.ml.optimization.activation.Activation;
import com.davidbracewell.apollo.ml.optimization.activation.SigmoidActivation;
import com.davidbracewell.apollo.ml.optimization.activation.SoftmaxActivation;
import com.davidbracewell.tuple.Tuple2;
import lombok.val;

/**
 * @author David B. Bracewell
 */
public class OutputLayer extends WeightLayer {
   public OutputLayer(int inputSize, int outputSize, Activation activation, NDArrayInitializer NDArrayInitializer, double l1, double l2) {
      super(inputSize, outputSize, activation, NDArrayInitializer, l1, l2);
   }

   public OutputLayer(WeightLayer layer) {
      super(layer);
   }

   public static Builder builder() {
      return new Builder();
   }

   public static Builder sigmoid() {
      return new Builder().activation(new SigmoidActivation());
   }

   public static Builder softmax() {
      return new Builder().activation(new SoftmaxActivation());
   }

   @Override
   public Tuple2<NDArray, Double> backward(WeightUpdate updater, NDArray input, NDArray output, NDArray delta, int iteration, boolean calcuateDelta) {
      return updater.update(this, input, output, delta, iteration, calcuateDelta);
   }

   @Override
   public BackpropResult backward(NDArray input, NDArray output, NDArray delta, boolean calculateDelta) {
      NDArray dzOut = calculateDelta
                      ? weights.T().mmul(delta)
                      : null;
      val dw = delta.mmul(input.T());
      val db = delta.sum(Axis.ROW);
      return BackpropResult.from(dzOut, dw, db);
   }

   @Override
   public NDArray backward(NDArray input, NDArray output, NDArray delta, double learningRate, int layerIndex, int iteration) {
      NDArray dzOut = layerIndex > 0
                      ? weights.T().mmul(delta)
                      : null;
      val dw = delta.mmul(input.T())
                    .divi(input.numCols());
      val db = delta.sum(Axis.ROW)
                    .divi(input.numCols());
      v.muli(0.9).subi(dw.muli(learningRate));
      weights.addi(v);
      bias.subi(db.muli(learningRate));
      l1Update(learningRate, iteration);
      return dzOut;
   }

   @Override
   public Layer copy() {
      return new OutputLayer(this);
   }

   public static class Builder extends WeightLayerBuilder<Builder> {

      @Override
      public Layer build() {
         boolean isBinary = getOutputSize() <= 2;
         return new OutputLayer(getInputSize(), isBinary ? 1 : getOutputSize(), getActivation(), this.getInitializer(),
                                getL1(),
                                getL2());
      }
   }

}// END OF OutputLayer
