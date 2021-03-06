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

package com.davidbracewell.apollo.stat.measure;

import com.davidbracewell.apollo.Optimum;
import com.davidbracewell.apollo.linear.NDArray;
import com.davidbracewell.apollo.linear.NDArrayFactory;
import lombok.NonNull;

import java.io.Serializable;

/**
 * <p>Calculates a metric between items, such as distance and similarity.</p>
 *
 * @author David B. Bracewell
 */
public interface Measure extends Serializable {

   /**
    * Calculate this measure using two double arrays as the input
    *
    * @param v1 the first double array
    * @param v2 the second double array
    * @return the metric result
    */
   default double calculate(@NonNull double[] v1, @NonNull double[] v2) {
      return calculate(NDArrayFactory.wrap(v1), NDArrayFactory.wrap(v2));
   }

   /**
    * Calculate this measure using two vectors as the input
    *
    * @param v1 the first vector
    * @param v2 the second vector
    * @return the metric result
    */
   double calculate(@NonNull NDArray v1, @NonNull NDArray v2);


   /**
    * Gets what kind of optimum should be used with this measure, i.e. is bigger or smaller better.
    *
    * @return the optimum
    */
   Optimum getOptimum();

}//END OF Measure
