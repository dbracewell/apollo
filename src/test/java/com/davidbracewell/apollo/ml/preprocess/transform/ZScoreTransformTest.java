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

package com.davidbracewell.apollo.ml.preprocess.transform;

import com.davidbracewell.apollo.ml.Instance;
import com.davidbracewell.apollo.ml.data.Dataset;
import com.davidbracewell.apollo.ml.preprocess.BaseInstancePreprocessorTest;
import com.davidbracewell.io.Resources;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.json.JSONReader;
import com.davidbracewell.io.structured.json.JSONWriter;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class ZScoreTransformTest extends BaseInstancePreprocessorTest {
   @Test
   public void test() throws Exception {
      ZScoreTransform transform = new ZScoreTransform("sepal_length");

      assertFalse(transform.trainOnly());

      Dataset<Instance> ds = getData(transform).encode();
      Collection<Object> featureNames = ds.getFeatureEncoder().values();
      assertTrue(featureNames.contains("petal_length"));
      assertTrue(featureNames.contains("petal_width"));
      assertTrue(featureNames.contains("sepal_length"));
      assertTrue(featureNames.contains("sepal_width"));

      Instance ii = ds.stream().first().orElse(Instance.create(Collections.emptyList()));
      assertEquals(-0.89, ii.getValue("sepal_length"), 0.01);

      transform = new ZScoreTransform();
      ds = getData(transform);
      ds.encode();
      featureNames = ds.getFeatureEncoder().values();
      assertTrue(featureNames.contains("petal_length"));
      assertTrue(featureNames.contains("petal_width"));
      assertTrue(featureNames.contains("sepal_length"));
      assertTrue(featureNames.contains("sepal_width"));

      ii = ds.stream().first().orElse(Instance.create(Collections.emptyList()));
      assertEquals(0.82, ii.getValue("sepal_length"), 0.01);
      assertEquals(0.02, ii.getValue("sepal_width"), 0.01);
      assertEquals(-1.04, ii.getValue("petal_length"), 0.01);
      assertEquals(-1.65, ii.getValue("petal_width"), 0.01);
   }

   @Test
   public void readWrite() throws Exception {
      ZScoreTransform transform = new ZScoreTransform("sepal_length");
      Resource out = Resources.fromString();
      try (JSONWriter writer = new JSONWriter(out)) {
         writer.beginDocument();
         writer.beginObject("filter");
         transform.write(writer);
         writer.endObject();
         writer.endDocument();
      }
      transform = new ZScoreTransform();
      try (JSONReader reader = new JSONReader(out)) {
         reader.beginDocument();
         reader.beginObject("filter");
         transform.read(reader);
         reader.endObject();
         reader.endDocument();
      }
      Dataset<Instance> ds = getData(transform).encode();
      Collection<Object> featureNames = ds.getFeatureEncoder().values();
      assertTrue(featureNames.contains("petal_length"));
      assertTrue(featureNames.contains("petal_width"));
      assertTrue(featureNames.contains("sepal_length"));
      assertTrue(featureNames.contains("sepal_width"));

      Instance ii = ds.stream().first().orElse(Instance.create(Collections.emptyList()));
      assertEquals(-0.89, ii.getValue("sepal_length"), 0.01);
   }
}