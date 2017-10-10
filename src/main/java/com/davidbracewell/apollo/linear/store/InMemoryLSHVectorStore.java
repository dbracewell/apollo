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

package com.davidbracewell.apollo.linear.store;

import com.davidbracewell.apollo.linear.NDArray;
import lombok.NonNull;
import org.apache.mahout.math.map.OpenIntObjectHashMap;
import org.apache.mahout.math.map.OpenObjectIntHashMap;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Implementation of an LSH vector store in which vectors are stored in memory.</p>
 *
 * @param <KEY> the type parameter
 * @author David B. Bracewell
 */
public class InMemoryLSHVectorStore<KEY> extends LSHVectorStore<KEY> {
   private static final long serialVersionUID = 1L;
   private final AtomicInteger vectorIDGenerator = new AtomicInteger();
   private final OpenIntObjectHashMap<NDArray> vectorIDMap = new OpenIntObjectHashMap<>();
   private final OpenObjectIntHashMap<KEY> keys = new OpenObjectIntHashMap<>();

   /**
    * Instantiates a new in-memory LSH vector store
    *
    * @param lsh the <code>InMemoryLSH</code> to use to build the vector store.
    */
   public InMemoryLSHVectorStore(@NonNull InMemoryLSH lsh) {
      super(lsh);
   }

   @Override
   public boolean containsKey(KEY key) {
      return keys.containsKey(key);
   }

   public VectorStore<KEY> createNew() {
      return InMemoryLSH.<KEY>builder()
                .dimension(this.dimension())
                .bands(this.lsh.getBands())
                .buckets(this.lsh.getBuckets())
                .signatureFunction(this.lsh.getSignatureFunction())
                .createVectorStore();
   }

   @Override
   protected int getID(KEY key) {
      return keys.get(key);
   }

   @Override
   protected NDArray getVectorByID(int id) {
      return vectorIDMap.get(id);
   }

   @Override
   public Iterator<NDArray> iterator() {
      return Collections.unmodifiableCollection(vectorIDMap.values()).iterator();
   }

   @Override
   public Collection<KEY> keys() {
      return keys.keys();
   }

   @Override
   protected int nextUniqueID() {
      return vectorIDGenerator.getAndIncrement();
   }

   @Override
   protected void registerVector(NDArray vector, int id) {
      keys.put(vector.getLabel(), id);
      vectorIDMap.put(id, vector);
   }

   @Override
   protected void removeVector(NDArray vector, int id) {
      vectorIDMap.removeKey(id);
      keys.removeKey(vector.getLabel());
   }

   @Override
   public int size() {
      return vectorIDMap.size();
   }

}// END OF LSHVectorStore