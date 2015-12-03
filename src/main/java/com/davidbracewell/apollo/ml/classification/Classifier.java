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

package com.davidbracewell.apollo.ml.classification;

import com.davidbracewell.apollo.linalg.Vector;
import com.davidbracewell.apollo.ml.Encoder;
import com.davidbracewell.apollo.ml.Featurizer;
import com.davidbracewell.apollo.ml.Instance;
import com.davidbracewell.apollo.ml.Model;
import com.davidbracewell.conversion.Cast;
import com.google.common.base.Preconditions;
import lombok.NonNull;

/**
 * The interface Classifier.
 *
 * @author David B. Bracewell
 */
public abstract class Classifier extends Model {
  private static final long serialVersionUID = 1L;
  private Featurizer featurizer;

  /**
   * Instantiates a new Classifier.
   *
   * @param labelEncoder   the label encoder
   * @param featureEncoder the feature encoder
   */
  protected Classifier(Encoder labelEncoder, Encoder featureEncoder) {
    super(labelEncoder, featureEncoder);
  }


  /**
   * Classifier result classifier result.
   *
   * @param input the input
   * @return the classifier result
   */
  public ClassifierResult classify(@NonNull Object input) {
    Preconditions.checkNotNull(featurizer, "Featurizer has not been set on the classifier");
    return classify(featurizer.extract(Cast.as(input)));
  }


  /**
   * Classify classifier result.
   *
   * @param instance the instance
   * @return the classifier result
   */
  public final ClassifierResult classify(@NonNull Instance instance) {
    return classify(instance.toVector(featureEncoder()));
  }

  /**
   * Classify classifier result.
   *
   * @param vector the vector
   * @return the classifier result
   */
  public abstract ClassifierResult classify(Vector vector);

  /**
   * Sets featurizer.
   *
   * @param featurizer the featurizer
   */
  public void setFeaturizer(Featurizer featurizer) {
    this.featurizer = featurizer;
  }
}//END OF Classifier