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

/**
 * <p>Different solvers (optimizers) that can be used with the {@link CRFTrainer}</p>
 *
 * @author David B. Bracewell
 */
public enum Solver {
   /**
    * LBFGS solver
    */
   LBFGS("lbfgs"),
   /**
    * L2 regularized Stochastic Gradient Descent solver.
    */
   L2SGD("l2sgd"),
   /**
    * Average perceptron.
    */
   AVERAGE_PERCEPTRON("ap"),
   /**
    * Passive aggressive solver.
    */
   PASSIVE_AGGRESSIVE("pa"),
   /**
    * Adaptive regularization solver.
    */
   ADAPTIVE_REGULARIZATION("arow");

   final String parameterSetting;

   Solver(String parameterSetting) {
      this.parameterSetting = parameterSetting;
   }

}//END OF Solver
