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

import com.davidbracewell.apollo.ml.Feature;
import com.davidbracewell.apollo.ml.Featurizer;
import lombok.NonNull;

import java.util.HashSet;
import java.util.Set;

/**
 * @author David B. Bracewell
 */
public class WindowFeaturizer<E> implements SequenceFeaturizer<E> {
   private static final long serialVersionUID = 1L;
   public static final String PREVIOUS_PREFIX = "P";
   public static final String NEXT_PREFIX = "P";

   private final int previousWindow;
   private final int nextWindow;
   private final Featurizer<? super E> featurizer;

   public WindowFeaturizer(int previousWindow, int nextWindow, @NonNull Featurizer<? super E> featurizer) {
      this.previousWindow = Math.abs(previousWindow);
      this.nextWindow = Math.abs(nextWindow);
      this.featurizer = featurizer;
   }


   @Override
   public Set<Feature> apply(ContextualIterator<E> iterator) {
      Set<Feature> features = new HashSet<>();

      features.addAll(featurizer.apply(iterator.getCurrent()));

      for (int i = 1; i <= previousWindow; i++) {
         String prefix = PREVIOUS_PREFIX + i + "::";
         iterator.getPrevious(i)
                 .ifPresent(v -> featurizer.apply(v)
                                           .forEach(f -> features.add(Feature.real(prefix + f.getName(), f.getValue())))
                           );
      }

      for (int i = 1; i < nextWindow; i++) {
         String prefix = NEXT_PREFIX + i + "::";
         iterator.getNext(i)
                 .ifPresent(v -> featurizer.apply(v)
                                           .forEach(f -> features.add(Feature.real(prefix + f.getName(), f.getValue())))
                           );
      }

      return features;
   }
}//END OF WindowFeaturizer
