package me.paul.luminescence

import scala.annotation.tailrec
import scala.util.Random

object LoopUtils {

    @tailrec
    final def whileLoop(condition: => Boolean)(func: => Unit): Unit = {
        if (condition) {
            func
            whileLoop(condition)(func)
        }
    }

    @tailrec
    final def whileYield[A](condition: => Boolean)(func: => A): A = {
        if (!condition) {
            func
        } else {
            whileYield(condition)(func)
        }
    }

    @tailrec
    final def doWhile[A](condition: A => Boolean)(func: => A): Unit = {
        val newA = func
        if (condition(newA)) {
            doWhile(condition)(func)
        }
    }

    @tailrec
    final def doWhileYield[A](condition: A => Boolean)(func: => A): A = {
        val newA = func
        if (!condition(newA)) {
            newA
        } else {
            doWhileYield(condition)(func)
        }
    }

    def untilLoop(condition: => Boolean)(func: => Unit): Unit = whileLoop(!condition)(func)

    def untilYield[A](condition: => Boolean)(func: => A): A = whileYield(!condition)(func)

    def doUntil[A](condition: A => Boolean)(func: => A): Unit = doWhileYield[A]((a: A) => !condition(a))(func)

    def doUntilYield[A](condition: A => Boolean)(func: => A): A = doWhileYield[A]((a: A) => !condition(a))(func)

    def randomBetween(lb: Double)(ub: Double): Double = {
        Random.nextDouble() * (ub - lb) + lb
    }

    def randomBounds(bounds: (Double, Double)): Double = randomBetween(bounds._1)(bounds._2)

    def randomUpTo(ub: Double): Double = randomBetween(0.0)(ub)

    def randomVariation(v: Double): Double = randomBetween(-v)(v)

}
