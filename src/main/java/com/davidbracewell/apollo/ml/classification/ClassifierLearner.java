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

import com.davidbracewell.apollo.ml.Instance;
import com.davidbracewell.apollo.ml.Learner;
import com.davidbracewell.conversion.Cast;

import java.util.Map;

/**
 * Base class for learners that produce <code>Classifier</code>s and use <code>Instance</code>s as their example type.
 *
 * @author David B. Bracewell
 */
public abstract class ClassifierLearner extends Learner<Instance, Classifier> {
   private static final long serialVersionUID = 1L;

   @Override
   public ClassifierLearner setParameter(String name, Object value) {
      return Cast.as(super.setParameter(name, value));
   }

   @Override
   public ClassifierLearner setParameters(Map<String, Object> parameters) {
      return Cast.as(super.setParameters(parameters));
   }

}//END OF ClassifierLearner
