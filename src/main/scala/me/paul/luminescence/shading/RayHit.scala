package me.paul.luminescence.shading

import me.paul.luminescence.geometry.{Geometry, Ray3D}

class RayHit(val ray: Ray3D, val geometry: Geometry, val time: Double) {

}

object RayHit {

    def apply(r: Ray3D, g: Geometry, t: Double): RayHit = new RayHit(r, g, t)

}
