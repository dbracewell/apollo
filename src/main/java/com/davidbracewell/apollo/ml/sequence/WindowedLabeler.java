package com.davidbracewell.apollo.ml.sequence;

import com.davidbracewell.apollo.ml.Encoder;
import com.davidbracewell.apollo.ml.Feature;
import com.davidbracewell.apollo.ml.FeatureVector;
import com.davidbracewell.apollo.ml.LabelEncoder;
import com.davidbracewell.apollo.ml.classification.Classifier;
import com.davidbracewell.apollo.ml.preprocess.PreprocessorList;
import lombok.NonNull;

import java.util.Iterator;

/**
 * <p>Greedy sequence labeler that wraps a standard {@link Classifier} and optimizing for local instead of global
 * structure.</p>
 *
 * @author David B. Bracewell
 */
public class WindowedLabeler extends SequenceLabeler {
   private static final long serialVersionUID = 1L;
   Classifier classifier;

   /**
    * Instantiates a new Model.
    *
    * @param labelEncoder       the label encoder
    * @param featureEncoder     the feature encoder
    * @param preprocessors      the preprocessors
    * @param transitionFeatures the transition features
    * @param validator          the validator
    */
   public WindowedLabeler(@NonNull LabelEncoder labelEncoder, @NonNull Encoder featureEncoder, @NonNull PreprocessorList<Sequence> preprocessors, @NonNull TransitionFeatures transitionFeatures, @NonNull SequenceValidator validator) {
      super(labelEncoder, featureEncoder, preprocessors, transitionFeatures, validator);
      super.setDecoder(new WindowDecoder());
   }

   @Override
   public double[] estimate(Iterator<Feature> observation, Iterator<String> transitions) {
      FeatureVector vector = new FeatureVector(classifier.getEncoderPair());
      observation.forEachRemaining(vector::set);
      transitions.forEachRemaining(t -> vector.set(t, 1.0));
      return classifier.classify(vector).distribution();
   }

   @Override
   public void setDecoder(@NonNull Decoder decoder) {
      throw new UnsupportedOperationException();
   }

}// END OF WindowedLabeler
