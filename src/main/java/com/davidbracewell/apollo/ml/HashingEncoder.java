package com.davidbracewell.apollo.ml;

import com.davidbracewell.apollo.ml.data.Dataset;
import com.davidbracewell.io.structured.StructuredReader;
import com.davidbracewell.io.structured.StructuredWriter;
import com.davidbracewell.stream.MStream;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * <p>An encoder that hashes features names allowing for the feature space to be reduced to a predetermined number of
 * features.</p>
 *
 * @author David B. Bracewell
 */
public class HashingEncoder implements Encoder, Serializable {
   private static final long serialVersionUID = 1L;
   private int numberOfFeatures = Integer.MAX_VALUE;

   /**
    * Instantiates a new Hashing encoder with a default of 3000 features.
    */
   public HashingEncoder() {
      this(3000);
   }

   /**
    * Instantiates a new Hashing encoder.
    *
    * @param numberOfFeatures the number of features
    */
   public HashingEncoder(int numberOfFeatures) {
      Preconditions.checkArgument(numberOfFeatures > 0, "Must allow at least one feature.");
      this.numberOfFeatures = numberOfFeatures;
   }

   @Override
   public Encoder createNew() {
      return new HashingEncoder(numberOfFeatures);
   }

   @Override
   public Object decode(double value) {
      return null;
   }

   @Override
   public double encode(@NonNull Object object) {
      return (object.hashCode() & 0x7fffffff) % numberOfFeatures;
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
   public void read(StructuredReader reader) throws IOException {
      this.numberOfFeatures = reader.nextKeyValue("numberOfFeatures").asIntegerValue();
   }

   @Override
   public int size() {
      return numberOfFeatures;
   }

   @Override
   public void unFreeze() {

   }

   @Override
   public List<Object> values() {
      return Collections.emptyList();
   }

   @Override
   public void write(StructuredWriter writer) throws IOException {
      writer.writeKeyValue("numberOfFeatures", numberOfFeatures);
   }

}// END OF HashingEncoder
