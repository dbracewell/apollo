package com.davidbracewell.apollo.ml.classification.bayes;

import com.davidbracewell.apollo.linalg.DynamicSparseVector;
import com.davidbracewell.apollo.linalg.Vector;
import com.davidbracewell.apollo.ml.Dataset;
import com.davidbracewell.apollo.ml.Instance;
import com.davidbracewell.apollo.ml.classification.ClassifierLearner;
import com.davidbracewell.collection.Collect;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.SerializableIntSupplier;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author David B. Bracewell
 */
public class BernoulliNaiveBayesLearner<T> implements ClassifierLearner {

  private List<DynamicSparseVector> ensureSize(List<DynamicSparseVector> list, int size, SerializableIntSupplier supplier) {
    while (list.size() <= size) {
      list.add(new DynamicSparseVector(supplier));
    }
    return list;
  }

  @Override
  public NaiveBayes train(@NonNull Dataset<Instance> dataset) {
    NaiveBayes model = new BernoulliNaiveBayes(
      Cast.as(dataset.labelEncoder()),
      dataset.featureEncoder()
    );
    model.labelEncoder();
    model.featureEncoder().freeze();

    Iterator<Instance> instanceIterator = dataset.iterator();
    double N = 0;
    DynamicSparseVector priors = new DynamicSparseVector(model::numberOfLabels);
    List<DynamicSparseVector> conditionals = new ArrayList<>();

    while (instanceIterator.hasNext()) {
      Instance instance = instanceIterator.next();
      if (instance.hasLabel()) {
        N++;
        int ci = (int) model.labelEncoder().encode(instance.getLabel().toString());
        priors.increment(ci);
        Vector vector = instance.toVector(dataset.featureEncoder());
        for (Vector.Entry entry : Collect.asIterable(vector.nonZeroIterator())) {
          ensureSize(conditionals, entry.index, model::numberOfLabels).get(entry.index).increment(ci);
        }
      }
    }

    model.priors = new double[model.numberOfLabels()];
    model.conditionals = new double[dataset.featureEncoder().size()][model.numberOfLabels()];

    for (int f = 0; f < dataset.featureEncoder().size(); f++) {
      for (int i = 0; i < model.numberOfLabels(); i++) {
        model.conditionals[f][i] = (conditionals.get(f).get(i) + 1) / (priors.get(i) + 2);
      }
    }

    for (int i = 0; i < model.priors.length; i++) {
      model.priors[i] = priors.get(i) / N;
    }

    return model;
  }

}// END OF BernoulliNaiveBayesLearner