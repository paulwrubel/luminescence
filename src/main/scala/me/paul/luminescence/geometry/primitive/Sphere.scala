package me.paul.luminescence.geometry.primitive

import me.paul.luminescence.geometry._
import me.paul.luminescence.shading.RayHit
import me.paul.luminescence.shading.material.Material

class Sphere(val center: Point3D, val radius: Double, val material: Material) extends Geometry {

    override def intersections(ray: Ray3D, tMin: Double, tMax: Double): Option[RayHit] = {
        val centerToStart = center to ray.origin
        val a: Double = ray.direction dot ray.direction
        val b: Double = ray.direction dot centerToStart
        val c: Double = (centerToStart dot centerToStart) - (radius * radius)

        val preDiscriminant: Double = (b * b) - (a * c)

        if (preDiscriminant > 0) {
            // evaluate first solution, which will be smaller
            val t1: Double = (-b - math.sqrt(preDiscriminant)) / a
            // return if within range
            if (t1 >= tMin && t1 <= tMax) {
                return Some(RayHit(ray, normalAt(ray.pointAt(t1)), t1, material))
            }
            //evaluate and return second solution if in range
            val t2 = (-b + math.sqrt(preDiscriminant)) / a
            if (t2 >= tMin && t2 <= tMax) {
                return Some(RayHit(ray, normalAt(ray.pointAt(t2)), t2, material))
            }
        }
        // default to no solution / out of bounds solution
        None
    }

    def normalAt(point: Point3D): Vector3D = {
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
