package me.paul.luminescence.geometry

import me.paul.luminescence.shading.ShadedColor

class Sphere(val center: Point3D, val radius: Double, val color: ShadedColor) extends Geometry {

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

    def apply(c: Point3D, r: Double, sc: ShadedColor): Sphere = new Sphere(c, r, sc)

}
