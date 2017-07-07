package com.davidbracewell.apollo.ml.classification;

import com.davidbracewell.apollo.ml.Instance;
import com.davidbracewell.apollo.ml.data.Dataset;
import com.davidbracewell.apollo.optimization.*;
import com.davidbracewell.apollo.optimization.activation.Activation;
import com.davidbracewell.apollo.optimization.activation.SoftmaxActivation;
import com.davidbracewell.apollo.optimization.loss.CrossEntropyLoss;
import com.davidbracewell.apollo.optimization.loss.LossFunction;
import com.davidbracewell.apollo.optimization.update.DeltaRule;
import com.davidbracewell.apollo.optimization.update.WeightUpdate;
import lombok.Getter;
import lombok.Setter;

/**
 * @author David B. Bracewell
 */
public class SoftmaxLearner extends ClassifierLearner {
   private final LossFunction loss = new CrossEntropyLoss();
   private final Activation activation = new SoftmaxActivation();
   @Getter
   @Setter
   private LearningRate learningRate = new ConstantLearningRate(0.1);
   @Getter
   @Setter
   private WeightUpdate weightUpdater = new DeltaRule();
   @Getter
   @Setter
   private int maxIterations = 300;
   @Getter
   @Setter
   private int batchSize = 20;
   @Getter
   @Setter
   private double tolerance = 1e-9;
   @Getter
   @Setter
   private boolean verbose = false;


   @Override
   public void resetLearnerParameters() {

   }

   @Override
   protected Classifier trainImpl(Dataset<Instance> dataset) {
      GLM model = new GLM(this);
      Optimizer optimizer = new SGD();
      WeightComponent component = new WeightComponent(new Weights(model.numberOfLabels(), model.numberOfFeatures(),
                                                                  WeightInitializer.DEFAULT));
      model.weights = optimizer.optimize(component,
                                         dataset::asVectors,
                                         new GradientDescentCostFunction(loss, activation),
                                         TerminationCriteria.create()
                                                            .maxIterations(maxIterations)
                                                            .historySize(3)
                                                            .tolerance(tolerance),
                                         learningRate,
                                         weightUpdater,
                                         verbose).getComponents().get(0);
      model.activation = activation;
      return model;
   }
}// END OF SoftmaxLearner
