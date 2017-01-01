/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.davidbracewell.apollo.ml.sequence;

import com.davidbracewell.apollo.linalg.Vector;
import com.davidbracewell.apollo.ml.Encoder;
import com.davidbracewell.apollo.ml.Feature;
import com.davidbracewell.apollo.ml.LabelEncoder;
import com.davidbracewell.apollo.ml.preprocess.PreprocessorList;
import lombok.NonNull;

import java.util.Iterator;

/**
 * <p>Structured Perceptron, or Collins Tagger, implementation.</p>
 *
 * @author David B. Bracewell
 */
public class StructuredPerceptron extends SequenceLabeler {
   private static final long serialVersionUID = 7255108354079743179L;
   /**
    * The Number of classes.
    */
   final int numberOfClasses;
   /**
    * The Weights.
    */
   Vector[] weights;

   /**
    * Instantiates a new Structured perceptron.
    *
    * @param labelEncoder       the label encoder
    * @param featureEncoder     the feature encoder
    * @param preprocessors      the preprocessors
    * @param transitionFeatures the transition features
    * @param validator          the validator
    */
   public StructuredPerceptron(@NonNull LabelEncoder labelEncoder, @NonNull Encoder featureEncoder, @NonNull PreprocessorList<Sequence> preprocessors, @NonNull TransitionFeatures transitionFeatures, @NonNull SequenceValidator validator) {
      super(labelEncoder, featureEncoder, preprocessors, transitionFeatures, validator);
      this.numberOfClasses = labelEncoder.size();
   }

   @Override
   public double[] estimate(Iterator<Feature> observation, Iterator<String> transitions) {
      double[] distribution = new double[numberOfClasses];
      while (observation.hasNext()) {
         Feature feature = observation.next();
         int index = (int) getFeatureEncoder().encode(feature.getName());
         if (index != -1) {
            for (int ci = 0; ci < numberOfClasses; ci++) {
               distribution[ci] += weights[ci].get(index);
            }
         }
      }

      while (transitions.hasNext()) {
         int index = (int) getFeatureEncoder().encode(transitions.next());
         if (index != -1) {
            for (int ci = 0; ci < numberOfClasses; ci++) {
               distribution[ci] += weights[ci].get(index);
            }
         }
      }

      return distribution;
   }

}// END OF StructuredPerceptron