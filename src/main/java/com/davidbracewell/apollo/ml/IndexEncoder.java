package com.davidbracewell.apollo.ml;

import com.davidbracewell.apollo.ml.data.Dataset;
import com.davidbracewell.collection.index.HashMapIndex;
import com.davidbracewell.collection.index.Index;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.io.structured.StructuredReader;
import com.davidbracewell.io.structured.StructuredSerializable;
import com.davidbracewell.io.structured.StructuredWriter;
import com.davidbracewell.stream.MStream;
import lombok.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * An encoder backed by an <code>Index</code> allowing a finite number of objects to be mapped to double values.
 *
 * @author David B. Bracewell
 */
public class IndexEncoder implements Encoder, Serializable, StructuredSerializable {
   private static final long serialVersionUID = 1L;
   protected volatile Index<String> index = new HashMapIndex<>();
   protected volatile AtomicBoolean frozen = new AtomicBoolean(false);

   @Override
   public void read(StructuredReader reader) throws IOException {
      index.addAll(reader.nextCollection(ArrayList::new, "items", String.class));
      frozen.set(reader.nextKeyValue("isFrozen").asBooleanValue());
   }

   @Override
   public void write(StructuredWriter writer) throws IOException {
      writer.writeKeyValue("items", index);
      writer.writeKeyValue("isFrozen", frozen.get());
   }

   @Override
   public double get(Object object) {
      if (object == null) {
         return -1;
      } else if (object instanceof Collection) {
         Collection<?> collection = Cast.as(object);
         double idx = -1;
         for (Object o : collection) {
            idx = index.getId(o.toString());
         }
         return idx;
      }
      return index.getId(object.toString());
   }

   @Override
   public void fit(MStream<String> stream) {
      if (!isFrozen()) {
         this.index.addAll(
            stream
               .filter(Objects::nonNull)
               .distinct()
               .collect()
                          );
      }
   }

   @Override
   public void fit(@NonNull Dataset<? extends Example> dataset) {
      if (!isFrozen()) {
         this.index.addAll(
            dataset.stream()
                   .flatMap(ex -> ex.getFeatureSpace().filter(Objects::nonNull))
                   .filter(Objects::nonNull)
                   .distinct()
                   .collect());
      }
   }

   @Override
   public double encode(Object object) {
      if (object == null) {
         return -1;
      }
      if (object instanceof Collection) {
         Collection<?> collection = Cast.as(object);
         double idx = -1;
         for (Object o : collection) {
            if (!frozen.get()) {
               idx = index.add(o.toString());
            } else {
               idx = index.getId(o.toString());
            }
         }
         return idx;
      }
      String str = object.toString();
      if (str != null) {
         if (!frozen.get()) {
            return index.add(str);
         }
      }
      return index.getId(str);
   }

   @Override
   public Object decode(double value) {
      return index.get((int) value);
   }

   @Override
   public void freeze() {
      frozen.set(true);
   }

   @Override
   public void unFreeze() {
      frozen.set(false);
   }

   @Override
   public boolean isFrozen() {
      return frozen.get();
   }

   @Override
   public int size() {
      return index.size();
   }

   @Override
   public List<Object> values() {
      return Cast.cast(index.asList());
   }

   @Override
   public Encoder createNew() {
      return new IndexEncoder();
   }


}// END OF IndexEncoder
