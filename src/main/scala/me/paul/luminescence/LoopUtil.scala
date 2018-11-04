package me.paul.luminescence

import scala.annotation.tailrec

/**
  * A helper object for representing basic loop functionality with tail-recursive methods, true to a function programming style
  */

object LoopUtil {

    /**
      * Equivalent to:
      *
      * while (condition) {
      *     func
      * }
      *
      * @param condition the condition of the while loop
      * @param func the statement(s) to run in the loop
      */

    @tailrec
    final def whileLoop(condition: => Boolean)(func: => Unit): Unit = {
        if (condition) {
            func
            whileLoop(condition)(func)
        }
    }

    /**
      * Equivalent to:
      *
      * var a: A
      * while (condition) {
      *     a = func
      * }
      * return a
      *
      * @param condition the condition of the while loop
      * @param func the statement(s) to run in the loop which return a value with type A
      * @tparam A type of the return value
      * @return the result of func at the last loop iteration
      */

    @tailrec
    final def whileYield[A](condition: => Boolean)(func: => A): A = {
        if (!condition) {
            func
        } else {
            whileYield(condition)(func)
        }
    }

    /**
      * Equivalent to:
      *
      * do {
      *     func
      * } while (condition)
      *
      * @param condition a function on type A to determine state of while loop
      * @param func procedure to run to get type A (may be Unit)
      * @tparam A type of func return
      */

    @tailrec
    final def doWhile[A](condition: A => Boolean)(func: => A): Unit = {
        val newA = func
        if (condition(newA)) {
            doWhile(condition)(func)
        }
    }

    /**
      * Equivalent to:
      *
      * var a: A
      * do  {
      *     a = func
      * } while (condition)
      * return a
      *
      * @param condition a function on type A to determine state of while loop
      * @param func the statement(s) to run in the loop which return a value with type A
      * @tparam A type of the return value
      * @return the result of func at the last loop iteration
      */

    @tailrec
    final def doWhileYield[A](condition: A => Boolean)(func: => A): A = {
        val newA = func
        if (!condition(newA)) {
            newA
        } else {
            doWhileYield(condition)(func)
        }
    }

    /**
      * Equivalent to:
      *
      * while (!condition) {
      *     func
      * }
      *
      * @param condition the condition of the while loop
      * @param func the statement(s) to run in the loop
      */

    def untilLoop(condition: => Boolean)(func: => Unit): Unit = whileLoop(!condition)(func)

    /**
      * Equivalent to:
      *
      * var a: A
      * while (!condition) {
      *     a = func
      * }
      * return a
      *
      * @param condition the condition of the while loop
      * @param func the statement(s) to run in the loop which return a value with type A
      * @tparam A type of the return value
      * @return the result of func at the last loop iteration
      */

    def untilYield[A](condition: => Boolean)(func: => A): A = whileYield(!condition)(func)

    /**
      * Equivalent to:
      *
      * do {
      *     func
      * } while (!condition)
      *
      * @param condition a function on type A to determine state of while loop
      * @param func procedure to run to get type A (may be Unit)
      * @tparam A type of func return
      */

    def doUntil[A](condition: A => Boolean)(func: => A): Unit = doWhileYield[A]((a: A) => !condition(a))(func)

    /**
      * Equivalent to:
      *
      * var a: A
      * do  {
      *     a = func
      * } while (!condition)
      * return a
      *
      * @param condition a function on type A to determine state of while loop
      * @param func the statement(s) to run in the loop which return a value with type A
      * @tparam A type of the return value
      * @return the result of func at the last loop iteration
      */

    def doUntilYield[A](condition: A => Boolean)(func: => A): A = doWhileYield[A]((a: A) => !condition(a))(func)

}
