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

package com.davidbracewell.apollo.ml;

import com.davidbracewell.cache.Cached;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A binary featurizer extracts feature names and assigns them the value of TRUE, i.e. 1.0
 *
 * @param <T> the input type parameter
 * @author David B. Bracewell
 */
public abstract class BinaryFeaturizer<T> implements Featurizer<T> {
   private static final long serialVersionUID = 1L;

   @Override
   @Cached
   public final Set<Feature> apply(T t) {
      if (t == null) {
         return Collections.emptySet();
      }
      return applyImpl(t)
                .stream()
                .map(Feature::TRUE)
                .collect(Collectors.toCollection(LinkedHashSet::new));
   }

   /**
    * Simplifies the creation of binary features by allowing child classes to only have to return a set of features
    * names. Note that the values for all features are assumed to be TRUE, i.e. 1.0
    *
    * @param input the input to process
    * @return the set of feature names
    */
   protected abstract Set<String> applyImpl(T input);

}//END OF BinaryFeaturizer
