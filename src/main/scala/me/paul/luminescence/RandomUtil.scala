package me.paul.luminescence

import scala.util.Random

object RandomUtil {

    /**
      * Helper function for a random range
      *
      * @param lb lower bound of range
      * @param ub upper bound of range
      * @return value in range [lb, ub)
      */

    def randomBetween(lb: Double)(ub: Double): Double = {
        Random.nextDouble() * (ub - lb) + lb
    }

    /**
      * Helper function for a random range
      *
      * @param bounds 2-tuple containing bounds for range
      * @return value in range [bounds._1, bounds._2)
      */

    def randomBetween(bounds: (Double, Double)): Double = randomBetween(bounds._1)(bounds._2)

    /**
      * Helper function for a random range with lower bound of 0
      *
      * @param ub upper bound for range
      * @return value in range [0.0, ub)
      */

    def randomUpTo(ub: Double): Double = randomBetween(0.0)(ub)

    /**
      * Helper function for a random range
      *
      * @param v maximum variation to apply in either direction (negative or positive)
      * @return value in range [-v, v)
      */

    def randomVariation(v: Double): Double = randomBetween(-v)(v)

}
