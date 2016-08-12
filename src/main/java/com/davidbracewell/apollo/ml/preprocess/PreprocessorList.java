package com.davidbracewell.apollo.ml.preprocess;

import com.davidbracewell.apollo.ml.Example;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.io.structured.ArrayValue;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredReader;
import com.davidbracewell.io.structured.StructuredSerializable;
import com.davidbracewell.io.structured.StructuredWriter;
import lombok.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * The type Preprocessor list.
 *
 * @param <T> the type parameter
 */
public class PreprocessorList<T extends Example> extends ArrayList<Preprocessor<T>> implements StructuredSerializable, ArrayValue {
  private static final long serialVersionUID = 1L;


  /**
   * Instantiates a new Preprocessor list.
   */
  public PreprocessorList() {

  }

  /**
   * Instantiates a new Preprocessor list.
   *
   * @param preprocessors the preprocessors
   */
  public PreprocessorList(@NonNull Collection<Preprocessor<T>> preprocessors) {
    super(preprocessors);
  }

  /**
   * Empty preprocessor list.
   *
   * @param <T> the type parameter
   * @return the preprocessor list
   */
  public static <T extends Example> PreprocessorList<T> empty() {
    return new PreprocessorList<>();
  }

  /**
   * Create preprocessor list.
   *
   * @param <T>           the type parameter
   * @param preprocessors the preprocessors
   * @return the preprocessor list
   */
  @SafeVarargs
  public static <T extends Example> PreprocessorList<T> create(Preprocessor<T>... preprocessors) {
    PreprocessorList<T> list;
    if (preprocessors == null) {
      list = new PreprocessorList<>();
    } else {
      list = new PreprocessorList<T>(Arrays.asList(preprocessors));
    }
    return list;
  }

  /**
   * Apply t.
   *
   * @param example the example
   * @return the t
   */
  public T apply(T example) {
    if (isEmpty()) {
      return example;
    }
    T transformed = Cast.as(example);
    for (Preprocessor<T> preprocessor : this) {
      transformed = preprocessor.apply(transformed);
    }
    return transformed;
  }

  /**
   * Gets runtime only.
   *
   * @return the runtime only
   */
  public PreprocessorList<T> getModelProcessors() {
    return new PreprocessorList<>(
      stream()
        .filter(p -> !p.trainOnly())
        .collect(Collectors.toList()));
  }

  /**
   * Reset.
   */
  public void reset() {
    forEach(Preprocessor::reset);
  }

  @Override
  public void read(StructuredReader reader) throws IOException {
    clear();
    while (reader.peek() != ElementType.END_ARRAY) {
      reader.beginObject();
      Class<? extends Preprocessor<T>> clazz = Cast.as(reader.nextKeyValue("class").asClass());
      Preprocessor<T> preprocessor = reader.nextKeyValue(clazz).getV2();
      add(preprocessor);
      reader.endObject();
    }
  }

  @Override
  public void write(StructuredWriter writer) throws IOException {
    for (Preprocessor<?> p : this) {
      writer.beginObject();
      writer.writeKeyValue("class", p.getClass().getName());
      writer.writeKeyValue("data", p);
      writer.endObject();
    }
  }

}// END OF PreprocessorList
