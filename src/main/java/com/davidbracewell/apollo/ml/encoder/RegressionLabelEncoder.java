package com.davidbracewell.apollo.ml.encoder;

import com.davidbracewell.apollo.ml.Example;
import com.davidbracewell.apollo.ml.data.Dataset;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.guava.common.base.Preconditions;
import com.davidbracewell.stream.MStream;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * An encoder that expects to encode Numbers to values. If non-numbers are given, and
 * <code>IllegalArgumentException</code> is thrown.
 *
 * @author David B. Bracewell
 */
public class RegressionLabelEncoder implements Serializable, LabelEncoder {
   private static final long serialVersionUID = 1L;

   @Override
   public LabelEncoder createNew() {
      return new RegressionLabelEncoder();
   }

   @Override
   public Object decode(double value) {
      return value;
   }

   @Override
   public double encode(@NonNull Object object) {
      Preconditions.checkArgument(object instanceof Number, object.getClass() + " is not a valid Number");
      return Cast.<Number>as(object).doubleValue();
   }

   @Override
   public void fit(Dataset<? extends Example> dataset) {

   }

   @Override
   public void fit(MStream<String> stream) {

   }

   @Override
   public void freeze() {

   }

   @Override
   public double get(Object object) {
      return encode(object);
   }

   @Override
   public boolean isFrozen() {
      return true;
   }

   @Override
   public int size() {
      return 0;
   }

   @Override
   public void unFreeze() {

   }

   @Override
   public List<Object> values() {
      return Collections.emptyList();
   }

}// END OF RealEncoder
