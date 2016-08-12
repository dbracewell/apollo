package com.davidbracewell.apollo.ml;

import com.davidbracewell.apollo.ml.classification.Classifier;
import com.davidbracewell.apollo.ml.data.Dataset;
import com.davidbracewell.apollo.ml.sequence.Sequence;
import com.davidbracewell.apollo.ml.sequence.SequenceLabeler;
import com.davidbracewell.io.structured.StructuredReader;
import com.davidbracewell.io.structured.StructuredSerializable;
import com.davidbracewell.io.structured.StructuredWriter;
import com.davidbracewell.reflection.BeanMap;
import com.davidbracewell.reflection.Ignore;
import lombok.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * The type Learner.
 *
 * @param <T> the type parameter
 * @param <M> the type parameter
 * @author David B. Bracewell
 */
public abstract class Learner<T extends Example, M extends Model> implements Serializable, StructuredSerializable {

  /**
   * Builder learner builder.
   *
   * @param <T> the type parameter
   * @param <M> the type parameter
   * @return the learner builder
   */
  public static <T extends Example, M extends Model> LearnerBuilder<T, M> builder() {
    return new LearnerBuilder<>();
  }

  /**
   * Classification learner builder.
   *
   * @return the learner builder
   */
  public static LearnerBuilder<Instance, Classifier> classification() {
    return new LearnerBuilder<>();
  }

  /**
   * Sequence labeling learner builder.
   *
   * @return the learner builder
   */
  public static LearnerBuilder<Sequence, SequenceLabeler> sequenceLabeling() {
    return new LearnerBuilder<>();
  }

//  public static <T extends Example, M extends Model, R extends Learner<T, M>> R read(StructuredReader reader) throws IOException {
//    Class<?> clazz = reader.nextKeyValue("class").asClass();
//    return Cast.as(builder().learnerClass(Cast.as(clazz)).parameters(reader.nextMap("parameters")).build());
//  }


  /**
   * Train classifier.
   *
   * @param dataset the dataset
   * @return the classifier
   */
  public M train(@NonNull Dataset<T> dataset) {
    dataset.encode();
    M model = trainImpl(dataset);
    model.finishTraining();
    return model;
  }

  /**
   * Train m.
   *
   * @param dataset the dataset
   * @return the m
   */
  protected abstract M trainImpl(Dataset<T> dataset);

  /**
   * Gets parameters.
   *
   * @return the parameters
   */
  @Ignore
  public Map<String, ?> getParameters() {
    return new BeanMap(this);
  }

  /**
   * Sets parameters.
   *
   * @param parameters the parameters
   */
  @Ignore
  public void setParameters(@NonNull Map<String, Object> parameters) {
    new BeanMap(this).putAll(parameters);
  }

  /**
   * Sets parameter.
   *
   * @param name  the name
   * @param value the value
   */
  @Ignore
  public void setParameter(String name, Object value) {
    new BeanMap(this).put(name, value);
  }

  /**
   * Gets parameter.
   *
   * @param name the name
   * @return the parameter
   */
  @Ignore
  public Object getParameter(String name) {
    return new BeanMap(this).get(name);
  }

  /**
   * Reset.
   */
  public abstract void reset();

  @Override
  public void write(StructuredWriter writer) throws IOException {
    writer.writeKeyValue("class", getClass().getName());
    writer.writeKeyValue("parameters", getParameters());
  }

}// END OF Learner
