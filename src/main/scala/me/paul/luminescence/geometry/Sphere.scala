package me.paul.luminescence.geometry

import me.paul.luminescence.shading.RayHit
import me.paul.luminescence.shading.material.Material

class Sphere(val center: Point3D, val radius: Double, val material: Material) extends Geometry {

    override def intersections(ray: Ray3D, min: Double, max: Double): Option[RayHit] = {
        val centerToStart = center to ray.start
        val a: Double = ray.direction dot ray.direction
        val b: Double = ray.direction dot centerToStart
        val c: Double = (centerToStart dot centerToStart) - (radius * radius)

        val preDiscriminant: Double = (b * b) - (a * c)

        if (preDiscriminant > 0) {
            // evaluate first solution, which will be smaller
            val t1: Double = (-b - math.sqrt(preDiscriminant)) / a
            // return if within range
            if (t1 >= min && t1 <= max) {
                return Some(RayHit(ray, this, t1))
            }
            //evaluate and return second solution if in range
            val t2 = (-b + math.sqrt(preDiscriminant)) / a
            if (t2 >= min && t2 <= max) {
                return Some(RayHit(ray, this, t2))
            }
        }
        // default to no solution / out of bounds solution
        None
    }

    override def normalAt(point: Point3D): Vector3D = {
        (center to point).normalize
    }

    def diameter: Double = {
        radius * 2
    }

    def volume: Double = {
        (4.0 / 3.0) * math.Pi * (radius * radius * radius)
    }

    def surfaceArea: Double = {
        4.0 * math.Pi * (radius * radius)
    }

    def circumference: Double = {
        2.0 * math.Pi * radius
    }

    override def toString: String = {
        s"Sphere(c = $center, r = $radius)"
    }

    override def equals(obj: scala.Any): Boolean = {
        obj match {
            case s: Sphere =>
                center == s.center && radius == s.radius
            case _ =>
                false
        }
    }

}

object Sphere {

    def apply(c: Point3D, r: Double, m: Material): Sphere = new Sphere(c, r, m)

}
