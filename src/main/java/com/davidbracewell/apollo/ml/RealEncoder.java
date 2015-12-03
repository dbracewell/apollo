package com.davidbracewell.apollo.ml;

import com.davidbracewell.conversion.Cast;
import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * @author David B. Bracewell
 */
public class RealEncoder implements Encoder, Serializable {
  private static final long serialVersionUID = 1L;

  @Override
  public double encode(Object object) {
    Preconditions.checkArgument(object instanceof Number, object.getClass() + " is not a valid Number");
    return Cast.<Number>as(object).doubleValue();
  }

  @Override
  public Object decode(double value) {
    return value;
  }

  @Override
  public void freeze() {

  }

  @Override
  public void unFreeze() {

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
  public List<Object> values() {
    return Collections.emptyList();
  }

  @Override
  public Encoder createNew() {
    return new RealEncoder();
  }

}// END OF RealEncoder