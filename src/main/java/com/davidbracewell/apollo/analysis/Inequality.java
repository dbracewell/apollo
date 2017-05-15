package com.davidbracewell.apollo.analysis;

/**
 * Methods for determining the inequality of two doubles.
 *
 * @author David B. Bracewell
 */
public enum Inequality {
    GREATER_THAN {
        @Override
        public boolean compare(double beingCompared, double comparedAgainst) {
            return beingCompared > comparedAgainst;
        }
    }, GREATER_THAN_EQUAL {
        @Override
        public boolean compare(double beingCompared, double comparedAgainst) {
            return beingCompared >= comparedAgainst;
        }
    }, LESS_THAN {
        @Override
        public boolean compare(double beingCompared, double comparedAgainst) {
            return beingCompared < comparedAgainst;
        }
    }, LESS_THAN_EQUAL {
        @Override
        public boolean compare(double beingCompared, double comparedAgainst) {
            return beingCompared <= comparedAgainst;
        }
    };

    /**
     * Determines if the inequality holds for n1 when compared with n2. For example, is n1 > n2?
     *
     * @param beingCompared   The number being compared
     * @param comparedAgainst The number being compared against
     * @return true if the inequality holds
     */
    public abstract boolean compare(double beingCompared, double comparedAgainst);

}// END OF Inequality