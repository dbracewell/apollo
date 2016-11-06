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

package com.davidbracewell.apollo.distribution;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class BinomialTest {

   Binomial binomial;


   @Before
   public void setUp() throws Exception {
      binomial = new Binomial(5, 10);
   }

   @Test
   public void increment() throws Exception {
      Binomial b2 = new Binomial();

      b2.increment(0, 1); //add one failure
      b2.increment(1, 6); //add six successes
      b2.increment(0, 6); //add six failures

      b2.decrement(0, 1); //decrement one failure
      b2.decrement(1); //decrement one success
      b2.decrement(0, 1); //decrement one failure

      assertEquals(5, b2.getNumberOfSuccesses());
      assertEquals(5, b2.getNumberOfFailures());
      assertEquals(0.5, b2.probabilityOfSuccess(), 0.1);
      assertEquals(0.205, b2.probability(6), 0.01);


      b2.decrement(1, 100);
      assertEquals(0, b2.getNumberOfTrials());
      assertEquals(0, b2.getNumberOfFailures());
      assertEquals(0, b2.getNumberOfSuccesses());
   }

   @Test
   public void sample() throws Exception {
      int sum = 0;
      for (int i = 0; i < 10; i++) {
         sum += binomial.sample();
      }
      //Expect about 5 successes
      assertEquals(5.0, sum / 10.0, 1.5);
   }

   @Test
   public void probability() throws Exception {
      assertEquals(0.205, binomial.probability(6), 0.01);
   }

   @Test
   public void logProbability() throws Exception {
      assertEquals(Math.log(0.205), binomial.logProbability(6), 0.01);
   }

   @Test
   public void cumulativeProbability() throws Exception {
      assertEquals(0.62, binomial.cumulativeProbability(0, 5), 0.01);
      assertEquals(0.62, binomial.cumulativeProbability(5), 0.01);
   }

   @Test
   public void inverseCumulativeProbability() throws Exception {
      assertEquals(5, binomial.inverseCumulativeProbability(0.5), 0.01);
   }


   @Test
   public void stats() throws Exception {
      assertEquals(5, binomial.getMean(), 0);
      assertEquals(0, binomial.getMode(), 0);
      assertEquals(2.5, binomial.getVariance(), 0);
   }
}