package com.davidbracewell.apollo.ml.classification.nn;

import com.davidbracewell.apollo.linear.Axis;
import com.davidbracewell.apollo.linear.NDArray;
import com.davidbracewell.apollo.ml.optimization.*;
import com.davidbracewell.function.SerializableSupplier;
import com.davidbracewell.guava.common.base.Stopwatch;
import com.davidbracewell.stream.MStream;
import com.davidbracewell.tuple.Tuple2;
import com.davidbracewell.tuple.Tuple3;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

import static com.davidbracewell.tuple.Tuples.$;

/**
 * @author David B. Bracewell
 */
public class Backprop implements Optimizer<FeedForwardNetwork> {
   private double loss = 0d;
   @Getter
   @Setter
   private int batchSize = 32;
   @Getter
   @Setter
   private int threads = 4;

   static float correct(NDArray predicted, NDArray gold) {
      int[] pMax = predicted.argMax(Axis.COlUMN);
      int[] gMax = gold.argMax(Axis.COlUMN);
      float correct = 0;
      for (int i = 0; i < pMax.length; i++) {
         if (pMax[i] == gMax[i]) {
            correct++;
         }
      }
      return correct;
   }

   @Override
   public double getFinalCost() {
      return loss;
   }

   @Override
   public void optimize(FeedForwardNetwork startingTheta,
                        SerializableSupplier<MStream<NDArray>> stream,
                        CostFunction<FeedForwardNetwork> costFunction,
                        TerminationCriteria terminationCriteria,
                        WeightUpdate weightUpdate,
                        int reportInterval
                       ) {

      BatchIterator data = new BatchIterator(stream.get().collect(),
                                             startingTheta.numberOfLabels(),
                                             startingTheta.numberOfFeatures());

      WeightUpdate[] layerUpdates = new WeightUpdate[startingTheta.layers.size()];
      for (int i = 0; i < layerUpdates.length; i++) {
         layerUpdates[i] = weightUpdate.copy();
      }
//
//      ExecutorService executor = Executors.newFixedThreadPool(threads);
      for (int iteration = 0; iteration < terminationCriteria.maxIterations(); iteration++) {
         loss = 0d;
//         data.shuffle();
//         List<Future<Tuple3<Double, Double, List<Layer>>>> futures = new ArrayList<>();
//         for (Iterator<NDArray> itr = data.iterator(batchSize); itr.hasNext(); ) {
//            layerUpdates = new WeightUpdate[startingTheta.layers.size()];
//            for (int i = 0; i < layerUpdates.length; i++) {
//               layerUpdates[i] = weightUpdate.copy();
//            }
//            WThread wt = new WThread(itr.next(),
//                                     startingTheta.copy(),
//                                     costFunction,
//                                     Arrays.asList(layerUpdates),
//                                     iteration
//            );
//            futures.add(executor.submit(wt));
//         }
//
//         futures.forEach(f -> {
//            try {
//               Tuple3<Double, Double, List<Layer>> t3 = f.get();
//               for (int i = 0; i < t3.v3.size(); i++) {
//                  startingTheta.layers.get(i).update(
//                     new NDArray[]{t3.v3.get(i).getWeights()},
//                     new NDArray[]{t3.v3.get(i).getBias()}
//                                                    );
//               }
//            } catch (Exception e) {
//               e.printStackTrace();
//            }
//         });

         List<Layer> layers = startingTheta.layers;
         val timer = Stopwatch.createStarted();
         for (Iterator<NDArray> itr = data.iterator(batchSize); itr.hasNext(); ) {
            NDArray X = itr.next();
            CostGradientTuple cgt = costFunction.evaluate(X, startingTheta);
            List<NDArray> ai = Arrays.asList(cgt.getActivations());
            NDArray dz = cgt.getGradient().getWeightGradient();
            loss += cgt.getCost();
            for (int i = layers.size() - 1; i >= 0; i--) {
               NDArray input = i == 0 ? X : ai.get(i - 1);
               Tuple2<NDArray, Double> t = layers.get(i).backward(layerUpdates[i], input, ai.get(i), dz, iteration,
                                                                  i > 0);
               dz = t.v1;
               if (i == layers.size() - 1) {
                  loss += t.v2 / X.numCols();
               }
            }
         }
         timer.stop();
         if (report(reportInterval, iteration, terminationCriteria, loss, timer.toString())) {
            break;
         }
      }

   }

   @Override
   public void reset() {
      loss = 0;
   }

   public static class WThread implements Callable<Tuple3<Double, Double, List<Layer>>> {
      final List<WeightUpdate> weightUpdates;
      final int iteration;
      public double loss;
      public double correct;
      public List<Layer> layers = new ArrayList<>();
      public FeedForwardNetwork network;
      public NDArray datum;
      CostFunction<FeedForwardNetwork> costFunction;

      public WThread(NDArray data,
                     FeedForwardNetwork network,
                     CostFunction<FeedForwardNetwork> costFunction,
                     List<WeightUpdate> weightUpdates,
                     int iteration
                    ) {
         for (Layer layer : network.layers) {
            layers.add(layer.copy());
         }
         this.network = network;
         this.datum = data;
         this.costFunction = costFunction;
         this.weightUpdates = weightUpdates;
         this.iteration = iteration;
      }

      @Override
      public Tuple3<Double, Double, List<Layer>> call() {
         double size = 0;
         NDArray X = datum;
         NDArray Y = datum.getLabelAsNDArray();
         double bSize = X.numCols();
         size += bSize;
         CostGradientTuple cgt = costFunction.evaluate(X, network);
         loss += cgt.getCost();
         List<NDArray> ai = Arrays.asList(cgt.getActivations());
         correct += correct(cgt.getActivations()[cgt.getActivations().length - 1], Y);
         NDArray dz = cgt.getGradient().getWeightGradient();
         for (int i = layers.size() - 1; i >= 0; i--) {
            NDArray input = i == 0 ? X : ai.get(i - 1);
            Tuple2<NDArray, Double> t = layers.get(i).backward(weightUpdates.get(i),
                                                               input,
                                                               ai.get(i),
                                                               dz,
                                                               iteration,
                                                               i > 0);
            dz = t.v1;
            if (i == layers.size() - 1) {
               loss += t.v2;
            }
         }
         loss = size > 0 ? loss / size : loss;
         return $(loss, correct, layers);
      }
   }
}// END OF Backprop
