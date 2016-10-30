package com.davidbracewell.apollo.linalg.decompose;

import com.davidbracewell.apollo.linalg.DenseMatrix;
import com.davidbracewell.apollo.linalg.Matrix;
import com.davidbracewell.conversion.Cast;
import lombok.Getter;
import org.jblas.DoubleMatrix;
import org.jblas.Singular;

import java.io.Serializable;

/**
 * <p>Truncated version of the svd that returns the top K singular values</p>
 *
 * @author David B. Bracewell
 */
public class TruncatedSVD implements Serializable, Decomposition {
   private static final long serialVersionUID = 1L;
   @Getter
   private final int size;

   /**
    * Instantiates a new Truncated svd.
    *
    * @param K the number of singular values
    */
   public TruncatedSVD(int K) {
      this.size = K;
   }

   @Override
   public Matrix[] decompose(Matrix m) {
      DenseMatrix dense;
      if (m instanceof DenseMatrix) {
         dense = Cast.as(m);
      } else {
         dense = new DenseMatrix(m);
      }
      DoubleMatrix[] result = Singular.sparseSVD(dense.asDoubleMatrix());
      DoubleMatrix u = result[0].getRange(0, result[0].getRows(), 0, size);
      DoubleMatrix s = DoubleMatrix.diag(result[1].getRange(0, size));
      DoubleMatrix v = result[2].getRange(0, result[2].getRows(), 0, size);
      return new DenseMatrix[]{new DenseMatrix(u), new DenseMatrix(s), new DenseMatrix(v)};
   }

}// END OF TruncatedSVD
