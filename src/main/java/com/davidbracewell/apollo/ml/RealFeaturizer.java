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

import com.davidbracewell.collection.Counter;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author David B. Bracewell
 */
public abstract class RealFeaturizer<T> implements Featurizer<T> {
  private static final long serialVersionUID = 1L;

  @Override
  public final Set<Feature> apply(T t) {
    return applyImpl(t).entries().stream().map(entry -> Feature.real(entry.getKey(), entry.getValue())).collect(Collectors.toSet());
  }

  protected abstract Counter<String> applyImpl(T t);


}//END OF RealFeaturizer