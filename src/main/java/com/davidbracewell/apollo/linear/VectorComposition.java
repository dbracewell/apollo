package com.davidbracewell.apollo.linear;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Collection;

/**
 * <p>Defines a method of combining multiple vectors into one</p>
 *
 * @author David B. Bracewell
 */
@FunctionalInterface
public interface VectorComposition {

   /**
    * Compose the given vectors with given k into a single vector.
    *
    * @param vectors the vectors to compose
    * @return the composed vector
    */
   default NDArray compose(@NonNull NDArray... vectors) {
      return compose(Arrays.asList(vectors));
   }

   /**
    * Compose the given vectors with given k into a single vector.
    *
    * @param vectors the vectors to compose
    * @return the composed vector
    */
   NDArray compose(Collection<NDArray> vectors);

}// END OF VectorComposition
