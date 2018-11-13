package me.paul.luminescence.geometry

import me.paul.luminescence.geometry.primitive.Sphere

class Ray3D(val origin: Point3D, _direction: Vector3D) {
    val direction: Vector3D = _direction.normalize

    def pointAt(t: Double): Point3D = {
        origin + (direction * t)
    }

    /*

    (d.d) t^2 + 2d.(e-c)t + (e-c).(e-c) - R^2 = 0

     */

    def intersections(s: Sphere): Option[(Double, Double)] = {
        val a: Double = direction dot direction
        val b: Double = (direction * 2) dot (origin - s.center)
        val c: Double = ((origin - s.center) dot (origin - s.center)) - (s.radius * s.radius)

        val preDiscriminant: Double = (b * b) - (4 * a * c)

        if (preDiscriminant < 0) {
            None
        } else {
            val discriminant: Double = math.sqrt(preDiscriminant)

            val solutionA: Double = (-b + discriminant) / (2 * a)
            val solutionB: Double = (-b - discriminant) / (2 * a)

            Some((solutionA, solutionB))
        }
    }

    override def toString: String = {
        s"Ray3D(s = $origin, d = $direction)"
    }
}

object Ray3D {

    def apply(s: Point3D, d: Vector3D): Ray3D = new Ray3D(s, d)

}