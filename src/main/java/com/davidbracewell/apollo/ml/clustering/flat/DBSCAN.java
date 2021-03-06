package com.davidbracewell.apollo.ml.clustering.flat;

import com.davidbracewell.apollo.linear.NDArray;
import com.davidbracewell.apollo.ml.clustering.ApacheClusterable;
import com.davidbracewell.apollo.ml.clustering.ApacheDistanceMeasure;
import com.davidbracewell.apollo.ml.clustering.Cluster;
import com.davidbracewell.apollo.ml.clustering.Clusterer;
import com.davidbracewell.apollo.stat.measure.Distance;
import com.davidbracewell.apollo.stat.measure.DistanceMeasure;
import com.davidbracewell.stream.MStream;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Clusters using Apache Math's implementation of the <a href="https://en.wikipedia.org/wiki/DBSCAN">DBSCAN</a>
 * algorithm.
 *
 * @author David B. Bracewell
 */
public class DBSCAN extends Clusterer<FlatClustering> {
   private static final long serialVersionUID = 1L;
   @Getter
   @Setter
   private double eps;
   @Getter
   @Setter
   private int minPts;
   @Getter
   @Setter(onParam = @_({@NonNull}))
   private DistanceMeasure distanceMeasure;


   /**
    * Instantiates a new DBSCAN Clusterer.
    */
   public DBSCAN() {
      this(Distance.Euclidean, 0.01, 2);
   }

   /**
    * Instantiates a new DBSCAN Clusterer.
    *
    * @param distanceMeasure the distance measure o use for clustering
    * @param eps             the maximum distance between two vectors to be in the same region
    * @param minPts          the minimum number of points to form  a dense region
    */
   public DBSCAN(@NonNull DistanceMeasure distanceMeasure, double eps, int minPts) {
      this.distanceMeasure = distanceMeasure;
      this.eps = eps;
      this.minPts = minPts;
   }


   @Override
   public FlatClustering cluster(MStream<NDArray> instances) {
      DBSCANClusterer<ApacheClusterable> clusterer = new DBSCANClusterer<>(eps, minPts,
                                                                           new ApacheDistanceMeasure(distanceMeasure));
      List<Cluster> clusters = clusterer.cluster(instances.map(ApacheClusterable::new).collect())
                                        .stream()
                                        .map(c -> {
                                           com.davidbracewell.apollo.ml.clustering.Cluster cp = new com.davidbracewell.apollo.ml.clustering.Cluster();
                                           c.getPoints().forEach(ap -> cp.addPoint(ap.getVector()));
                                           return cp;
                                        }).collect(Collectors.toList());
      FlatCentroidClustering clustering = new FlatCentroidClustering(this, distanceMeasure);
      clusters.forEach(clustering::addCluster);
      return clustering;
   }


}// END OF DBSCAN
