package com.davidbracewell.apollo.ml.clustering.flat;

import com.davidbracewell.apollo.linear.NDArray;
import com.davidbracewell.apollo.linear.NDArrayFactory;
import com.davidbracewell.apollo.linear.SparkLinearAlgebra;
import com.davidbracewell.apollo.ml.clustering.Cluster;
import com.davidbracewell.apollo.ml.clustering.Clusterer;
import com.davidbracewell.apollo.stat.measure.Distance;
import com.davidbracewell.stream.MStream;
import lombok.Getter;
import lombok.Setter;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.clustering.BisectingKMeansModel;
import scala.Tuple2;

import java.util.Map;

/**
 * The type Bisecting k means.
 *
 * @author David B. Bracewell
 */
public class BisectingKMeans extends Clusterer<FlatCentroidClustering> {
   @Getter
   @Setter
   private int K = 4;
   @Getter
   @Setter
   private int maxIterations = 20;
   @Getter
   @Setter
   private double minDivisibleClusterSize = 1.0;

   @Override
   public FlatCentroidClustering cluster(MStream<NDArray> instances) {
      org.apache.spark.mllib.clustering.BisectingKMeans learner = new org.apache.spark.mllib.clustering.BisectingKMeans();
      learner.setK(K);
      learner.setMaxIterations(maxIterations);
      learner.setMinDivisibleClusterSize(minDivisibleClusterSize);

      JavaRDD<org.apache.spark.mllib.linalg.Vector> rdd = SparkLinearAlgebra.toVectors(instances);
      BisectingKMeansModel model = learner.run(rdd);
      FlatCentroidClustering clustering = new FlatCentroidClustering(this, Distance.Euclidean);
      org.apache.spark.mllib.linalg.Vector[] centers = model.clusterCenters();
      for (int i = 0; i < K; i++) {
         clustering.addCluster(new Cluster());
         clustering.get(i).setCentroid(NDArrayFactory.wrap(centers[i].toArray()));
      }
      Map<org.apache.spark.mllib.linalg.Vector, Integer> assignments = rdd.mapToPair(
         v -> Tuple2.apply(v, model.predict(v))).collectAsMap();
      assignments.forEach((v, i) -> clustering.get(i).addPoint(NDArrayFactory.wrap(v.toArray())));

      return clustering;
   }
}// END OF BisectingKMeans
