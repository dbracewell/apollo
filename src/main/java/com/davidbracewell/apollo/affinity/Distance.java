package com.davidbracewell.apollo.affinity;


import com.google.common.collect.Sets;
import lombok.NonNull;

import java.util.Map;

import static com.davidbracewell.apollo.affinity.Similarity.DotProduct;

/**
 * The enum Distance measures.
 *
 * @author dbracewell
 */
public enum Distance implements DistanceMeasure {
  Euclidean {
    @Override
    public double calculate(@NonNull Map<?, ? extends Number> m1, @NonNull Map<?, ? extends Number> m2) {
      double m1Sq = DotProduct.calculate(m1, m1);
      double m2Sq = DotProduct.calculate(m2, m2);
      double m12Sq = DotProduct.calculate(m1, m2);
      return Math.sqrt(m1Sq + m2Sq - 2 * m12Sq);
    }
  },
  Manhattan {
    @Override
    public double calculate(@NonNull Map<?, ? extends Number> m1, @NonNull Map<?, ? extends Number> m2) {
      double sum = 0;
      for (Object o : Sets.union(m1.keySet(), m2.keySet())) {
        double d1 = m1.containsKey(o) ? m1.get(o).doubleValue() : 0d;
        double d2 = m2.containsKey(o) ? m2.get(o).doubleValue() : 0d;
        sum += Math.abs(d1 - d2);
      }
      return sum;
    }
  },
  Hamming {
    @Override
    public double calculate(@NonNull Map<?, ? extends Number> m1, @NonNull Map<?, ? extends Number> m2) {
      double sum = 0;
      for (Object o : Sets.union(m1.keySet(), m2.keySet())) {
        double d1 = m1.containsKey(o) ? m1.get(o).doubleValue() : 0d;
        double d2 = m2.containsKey(o) ? m2.get(o).doubleValue() : 0d;
        if (d1 != d2) {
          sum++;
        }
      }
      return sum;
    }
  },
  EarthMovers {
    @Override
    public double calculate(Map<?, ? extends Number> m1, Map<?, ? extends Number> m2) {
      double last = 0;
      double sum = 0;
      for (Object o : Sets.union(m1.keySet(), m2.keySet())) {
        double d1 = m1.containsKey(o) ? m1.get(o).doubleValue() : 0d;
        double d2 = m2.containsKey(o) ? m2.get(o).doubleValue() : 0d;
        double dist = (d1 + last) - d2;
        sum += Math.abs(dist);
        last = dist;
      }
      return sum;
    }
  },
  Chebyshev {
    @Override
    public double calculate(Map<?, ? extends Number> m1, Map<?, ? extends Number> m2) {
      double max = 0;
      for (Object o : Sets.union(m1.keySet(), m2.keySet())) {
        double d1 = m1.containsKey(o) ? m1.get(o).doubleValue() : 0d;
        double d2 = m2.containsKey(o) ? m2.get(o).doubleValue() : 0d;
        max = Math.max(max, Math.abs(d1 - d2));
      }
      return max;
    }
  },
  KLDivergence {
    @Override
    public double calculate(Map<?, ? extends Number> m1, Map<?, ? extends Number> m2) {
      double kl = 0;
      for (Object o : Sets.union(m1.keySet(), m2.keySet())) {
        double d1 = m1.containsKey(o) ? m1.get(o).doubleValue() : 0d;
        double d2 = m2.containsKey(o) ? m2.get(o).doubleValue() : 0d;
        if (d1 == 0.0 || d2 == 0.0) {
          continue;
        }
        kl += d1 * Math.log(d1 / d2);
      }
      return kl;
    }
  }


}//END OF Distance