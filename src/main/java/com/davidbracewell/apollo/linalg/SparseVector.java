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

package com.davidbracewell.apollo.linalg;

import com.davidbracewell.conversion.Cast;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.random.Well19937c;
import org.apache.mahout.math.map.OpenIntDoubleHashMap;

import java.io.Serializable;
import java.util.*;
import java.util.stream.IntStream;

/**
 * A sparse vector implementation backed by a map
 *
 * @author David B. Bracewell
 */
public class SparseVector implements Vector, Serializable {
   private static final long serialVersionUID = 1L;
   private final OpenIntDoubleHashMap map;
   private final int dimension;

   /**
    * Instantiates a new Sparse vector.
    *
    * @param dimension the dimension of the new vector
    */
   public SparseVector(int dimension) {
      Preconditions.checkArgument(dimension >= 0, "Dimension must be non-negative.");
      this.dimension = dimension;
      this.map = new OpenIntDoubleHashMap();
   }

   /**
    * Copy Constructor
    *
    * @param vector The vector to copy from
    */
   public SparseVector(@NonNull Vector vector) {
      this.dimension = vector.dimension();
      this.map = new OpenIntDoubleHashMap(vector.size());
      for (Iterator<Vector.Entry> itr = vector.nonZeroIterator(); itr.hasNext(); ) {
         Vector.Entry de = itr.next();
         this.map.put(de.index, de.value);
      }
   }

   /**
    * Static method to create a new <code>SparseVector</code> whose values are 1.
    *
    * @param dimension The dimension of the vector
    * @return a new <code>SparseVector</code> whose values are 1.
    */
   public static Vector ones(int dimension) {
      Vector v = new SparseVector(dimension);
      for (int i = 0; i < dimension; i++) {
         v.set(i, 1);
      }
      return v;
   }

   /**
    * Creates a new vector of the given dimension initialized with random values in the range of <code>[min,
    * max]</code>.
    *
    * @param dimension the dimension of the vector
    * @param min       the minimum assignable value
    * @param max       the maximum assignable value
    * @return the vector
    */
   public static Vector random(int dimension, double min, double max) {
      return random(dimension, min, max, new Well19937c());
   }


   /**
    * Creates a new vector of the given dimension initialized with random values in the range of <code>[min,
    * max]</code>.
    *
    * @param dimension the dimension of the vector
    * @param min       the minimum assignable value
    * @param max       the maximum assignable value
    * @param rnd       the random number generator to use generate values
    * @return the vector
    */
   public static Vector random(int dimension, double min, double max, @NonNull RandomGenerator rnd) {
      Preconditions.checkArgument(dimension >= 0, "Dimension must be non-negative");
      Preconditions.checkArgument(max > min, "Invalid Range [" + min + ", " + max + "]");
      SparseVector v = new SparseVector(dimension);
      for (int i = 0; i < dimension; i++) {
         v.set(i, rnd.nextDouble() * (max - min) + min);
      }
      return v;
   }

   /**
    * Constructs a new vector of given dimension with values randomized using a gaussian with mean 0 and standard
    * deviation of 1
    *
    * @param dimension the dimension of the vector
    * @return the vector
    */
   public static Vector randomGaussian(int dimension) {
      SparseVector v = new SparseVector(dimension);
      Random rnd = new Random();
      for (int i = 0; i < dimension; i++) {
         v.set(i, rnd.nextGaussian());
      }
      return v;
   }

   /**
    * Static method to create a new <code>SparseVector</code> whose values are 0.
    *
    * @param dimension The dimension of the vector
    * @return a new <code>SparseVector</code> whose values are 0.
    */
   public static Vector zeros(int dimension) {
      return new SparseVector(dimension);
   }

   @Override
   public Vector compress() {
      map.trimToSize();
      return this;
   }

   @Override
   public Vector copy() {
      return new SparseVector(this);
   }

   @Override
   public int dimension() {
      return dimension;
   }

   @Override
   public double get(int index) {
      Preconditions.checkPositionIndex(index, dimension());
      return map.get(index);
   }

   @Override
   public Vector increment(int index, double amount) {
      map.adjustOrPutValue(index, amount, amount);
      return this;
   }

   @Override
   public boolean isDense() {
      return false;
   }

   @Override
   public boolean isSparse() {
      return true;
   }

   @Override
   public Iterator<Vector.Entry> nonZeroIterator() {
      return new Iterator<Vector.Entry>() {
         private final PrimitiveIterator.OfInt indexIter = IntStream.of(map.keys().elements()).iterator();

         @Override
         public boolean hasNext() {
            return indexIter.hasNext();
         }

         @Override
         public Vector.Entry next() {
            if (!indexIter.hasNext()) {
               throw new NoSuchElementException();
            }
            int index = indexIter.next();
            return new Vector.Entry(index, get(index));
         }
      };
   }

   @Override
   public Iterator<Vector.Entry> orderedNonZeroIterator() {
      return new Iterator<Vector.Entry>() {
         private final PrimitiveIterator.OfInt indexIter = IntStream.of(map.keys().elements()).sorted().iterator();

         @Override
         public boolean hasNext() {
            return indexIter.hasNext();
         }

         @Override
         public Vector.Entry next() {
            if (!indexIter.hasNext()) {
               throw new NoSuchElementException();
            }
            int index = indexIter.next();
            return new Vector.Entry(index, get(index));
         }
      };
   }

   @Override
   public Vector set(int index, double value) {
      if (value == 0) {
         map.removeKey(index);
      } else {
         map.put(index, value);
      }
      return this;
   }

   @Override
   public int size() {
      return map.size();
   }

   @Override
   public Vector slice(int from, int to) {
      Preconditions.checkPositionIndex(from, dimension());
      Preconditions.checkPositionIndex(to, dimension() + 1);
      Preconditions.checkState(to > from, "To index must be > from index");
      SparseVector v = new SparseVector((to - from));
      for (int i = from; i < to; i++) {
         v.set(i, get(i));
      }
      return v;
   }

   @Override
   public double[] toArray() {
      final double[] d = new double[dimension()];
      map.forEachPair((i, v) -> {
         d[i] = v;
         return true;
      });
      return d;
   }

   @Override
   public Vector zero() {
      this.map.clear();
      return this;
   }

   @Override
   public Vector redim(int newDimension) {
      Vector v = new SparseVector(newDimension);
      for (Iterator<Vector.Entry> itr = nonZeroIterator(); itr.hasNext(); ) {
         Vector.Entry de = itr.next();
         v.set(de.index, de.value);
      }
      return v;
   }

   @Override
   public String toString() {
      return Arrays.toString(toArray());
   }


   @Override
   public boolean equals(Object o) {
      return o != null && o instanceof Vector && Arrays.equals(toArray(), Cast.<Vector>as(o).toArray());
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode(toArray());
   }
}//END OF SparseVector
