package com.davidbracewell.apollo.linear.decompose;

import com.davidbracewell.apollo.linear.NDArray;
import com.davidbracewell.apollo.linear.NDArrayFactory;
import com.davidbracewell.apollo.linear.SparkLinearAlgebra;
import com.davidbracewell.apollo.linear.dense.DenseDoubleNDArray;
import com.davidbracewell.apollo.linear.dense.DenseFloatNDArray;
import lombok.NonNull;
import org.jblas.DoubleMatrix;
import org.jblas.FloatMatrix;
import org.jblas.Singular;

import java.io.Serializable;

/**
 * @author David B. Bracewell
 */
public class SingularValueDecomposition implements Decomposition, Serializable {
   private static final long serialVersionUID = 1L;

   private final boolean distributed;
   private final boolean sparse;
   private final int K;

   public SingularValueDecomposition() {
      this(false, false, -1);
   }

   public SingularValueDecomposition(int K) {
      this(false, false, K);
   }

   public SingularValueDecomposition(boolean distributed, boolean sparse) {
      this(distributed, sparse, -1);
   }

   public SingularValueDecomposition(boolean distributed, boolean sparse, int K) {
      this.distributed = distributed;
      this.sparse = sparse;
      this.K = K;
   }

   public static void main(String[] args) {
      NDArray a = NDArrayFactory.DENSE_DOUBLE.rand(4, 5);
      SingularValueDecomposition svd = new SingularValueDecomposition(false, true);
      NDArray[] usv = svd.decompose(a);

      usv[0].pprint(System.out);
      usv[1].pprint(System.out);
      usv[2].pprint(System.out);

   }

   @Override
   public NDArray[] decompose(@NonNull NDArray input) {
      if (distributed) {
         return SparkLinearAlgebra.svd(input, K <= 0 ? input.numCols() : K);
      }

      NDArray[] result;
      if (input instanceof DenseFloatNDArray) {
         FloatMatrix[] r;
         if (sparse) {
            r = Singular.sparseSVD(input.toFloatMatrix());
         } else {
            r = Singular.fullSVD(input.toFloatMatrix());
         }
         result = new NDArray[]{
            new DenseFloatNDArray(r[0]),
            new DenseFloatNDArray(FloatMatrix.diag(r[1])),
            new DenseFloatNDArray(r[2]),
         };
      } else {
         DoubleMatrix[] r;
         if (sparse) {
            r = Singular.sparseSVD(input.toDoubleMatrix());
         } else {
            r = Singular.fullSVD(input.toDoubleMatrix());
         }
         result = new NDArray[]{
            new DenseDoubleNDArray(r[0]),
            new DenseDoubleNDArray(DoubleMatrix.diag(r[1])),
            new DenseDoubleNDArray(r[2]),
         };
      }

      if (K > 0) {
         result[0] = result[0].slice(0, result[0].numRows(), 0, K);
         result[1] = result[1].slice(0, K, 0, K);
         result[2] = result[2].slice(0, result[2].numRows(), 0, K);
      }

      return result;
   }
}// END OF SingularValueDecomposition
