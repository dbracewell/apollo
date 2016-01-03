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

package com.davidbracewell.apollo.affinity;

import com.davidbracewell.apollo.linalg.DenseVector;
import com.davidbracewell.apollo.linalg.Vector;
import com.davidbracewell.apollo.linalg.VectorMap;
import com.davidbracewell.collection.Counter;
import com.google.common.collect.Maps;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * The interface Measure.
 *
 * @author David B. Bracewell
 */
public interface Measure extends Serializable {

  /**
   * Calculate double.
   *
   * @param v1 the v 1
   * @param v2 the v 2
   * @return the double
   */
  default double calculate(@NonNull double[] v1, @NonNull double[] v2) {
    return calculate(DenseVector.wrap(v1), DenseVector.wrap(v2));
  }

  /**
   * Calculate double.
   *
   * @param v1 the v 1
   * @param v2 the v 2
   * @return the double
   */
  default double calculate(@NonNull Vector v1, @NonNull Vector v2) {
    return calculate(VectorMap.wrap(v1), VectorMap.wrap(v2));
  }

  /**
   * Calculate double.
   *
   * @param c1 the c 1
   * @param c2 the c 2
   * @return the double
   */
  default double calculate(@NonNull Counter<?> c1, @NonNull Counter<?> c2) {
    return calculate(c1.asMap(), c2.asMap());
  }

  /**
   * Calculate double.
   *
   * @param c1 the c 1
   * @param c2 the c 2
   * @return the double
   */
  default double calculate(@NonNull Set<?> c1, @NonNull Set<?> c2) {
    return calculate(Maps.asMap(c1, d -> 1), Maps.asMap(c2, d -> 1));
  }

  /**
   * Calculate double.
   *
   * @param m1 the m 1
   * @param m2 the m 2
   * @return the double
   */
  double calculate(Map<?, ? extends Number> m1, Map<?, ? extends Number> m2);

}//END OF Measure