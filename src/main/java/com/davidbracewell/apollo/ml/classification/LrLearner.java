package com.davidbracewell.apollo.ml.classification;

import com.davidbracewell.apollo.linalg.SparseVector;
import com.davidbracewell.apollo.linalg.Vector;
import com.davidbracewell.apollo.ml.EncoderPair;
import com.davidbracewell.apollo.ml.Feature;
import com.davidbracewell.apollo.ml.Instance;
import com.davidbracewell.apollo.ml.data.Dataset;
import com.davidbracewell.apollo.ml.data.source.DenseCSVDataSource;
import com.davidbracewell.apollo.ml.preprocess.PreprocessorList;
import com.davidbracewell.apollo.optimization.DecayLearningRate;
import com.davidbracewell.apollo.optimization.regularization.L1Regularizer;
import com.davidbracewell.io.Resources;
import com.davidbracewell.io.resource.Resource;
import lombok.NonNull;

import java.util.Random;

import static com.davidbracewell.apollo.ml.classification.ClassifierEvaluation.crossValidation;

/**
 * Stochastic Gradient Descent Training for L1-regularized Log-linear Models with Cumulative Penalty
 *
 * @author David B. Bracewell
 */
public class LrLearner extends BinaryClassifierLearner {

   public static void main(String[] args) {
      Resource url = Resources.from(
         "https://raw.githubusercontent.com/sjwhitworth/golearn/master/examples/datasets/iris_headers.csv");
      DenseCSVDataSource dataSource = new DenseCSVDataSource(url, true);
      dataSource.setLabelName("Species");
      Dataset<Instance> dataset = Dataset.classification()
                                         .source(dataSource)
                                         .shuffle(new Random(1234));
      crossValidation(dataset,
                      () -> new SoftmaxLearner()
                               .setParameter("learningRate", new DecayLearningRate(0.1, 0.001))
                               .setParameter("weightUpdater", new L1Regularizer(0.001))
                               .setParameter("batchSize", 0),
                      10
                     )
         .output(System.out);

      crossValidation(dataset,
                      () -> BinarySGDLearner.logisticRegression().oneVsRest().setParameter("normalize", true),
                      10
                     ).output(System.out);

      crossValidation(dataset,
                      () -> BinarySGDLearner.linearSVM().oneVsRest(),
                      10
                     ).output(System.out);


//      crossValidation(dataset,
//                      () -> new LibLinearLearner()
//                               .setParameter("solver", SolverType.L1R_LR)
//                               .setParameter("c", 1)
//                               .setParameter("eps", 0.001)
//                               .setParameter("bias", true),
//                      10
//                     )
//         .output(System.out);
//      MultiCounter<String, String> mm = new SGDLearner()
//                                           .setParameter("learningRate", new DecayLearningRate(0.1, 0.001))
//                                           .setParameter("weightUpdater", new L1Regularization(0.001))
//                                           .setParameter("activation", new SoftmaxFunction())
//                                           .setParameter("batchSize", 0)
//                                           .train(dataset)
//                                           .getModelParameters()
//                                           .transpose();
//      mm.firstKeys().forEach(k1 -> System.out.println(k1 + " : " + mm.get(k1)));
   }

   @Override
   public void reset() {

   }

   @Override
   protected LrClassifier trainForLabel(Dataset<Instance> dataset, double trueLabel) {
      LrClassifier model = new LrClassifier(dataset.getEncoderPair(),
                                            dataset.getPreprocessors());

      model.bias = 0;
      model.weights = new SparseVector(dataset.getEncoderPair().numberOfFeatures());
      final int N = 1;

      Vector q = new SparseVector(dataset.getEncoderPair().numberOfFeatures() + 1);
      double eta0 = 1;
      double C = 1;
      double u = 0;
      double tolerance = 0.000001;

      double previous = 0;

      for (int iteration = 1; iteration <= 200; iteration++) {
         double eta = (eta0 + 1.0) / (1.0 + ((double) iteration / (double) N));
         u = u + eta * C / N;
         double sumLogLikelihood = 0;
         for (Instance instance : dataset.shuffle()) {
            double y = dataset.getEncoderPair().encodeLabel(instance.getLabel()) == trueLabel ? 1 : 0;
            double yHat = model.classify(instance).distribution()[(int) y];
            double gradient = y - yHat;
            sumLogLikelihood += y * Math.log(yHat + 1e-24) + (1 - y) * Math.log(1 - yHat + 1e-24);
            if (gradient != 0) {
               for (Feature feature : instance) {
                  int fid = (int) dataset.getEncoderPair().encodeFeature(feature.getName());
                  model.weights.increment(fid, gradient * eta * feature.getValue());

                  double z = model.weights.get(fid);
                  double wi = model.weights.get(fid);
                  if (model.weights.get(fid) > 0) {
                     double v = Math.max(0, wi - (u + q.get(fid)));
                     model.weights.set(fid, v);
                  } else if (model.weights.get(fid) < 0) {
                     double v = Math.min(0, wi + (u - q.get(fid)));
                     model.weights.set(fid, v);
                  }
                  q.increment(fid, model.weights.get(fid) - z);
               }

               model.bias *= gradient * eta;
            }
         }
         sumLogLikelihood = -sumLogLikelihood;

         if (Math.abs(sumLogLikelihood - previous) <= tolerance) {
            break;
         }

         previous = sumLogLikelihood;
      }

      model.weights.mapDivideSelf(model.weights.l1Norm());
      return model;
   }

   public static class LrClassifier extends BinaryGLM {

      /**
       * Instantiates a new Classifier.
       *
       * @param encoderPair   the encoder pair
       * @param preprocessors the preprocessors
       */
      LrClassifier(EncoderPair encoderPair, @NonNull PreprocessorList<Instance> preprocessors) {
         super(encoderPair, preprocessors);
      }

      @Override
      public Classification classify(Vector vector) {
         double[] dist = new double[2];
         dist[1] = 1.0 / (1.0 + Math.exp(-(weights.dot(vector) + bias)));
         dist[0] = 1.0 - dist[1];
         return createResult(dist);
      }
   }
}// END OF LrLearner