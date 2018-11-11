package me.paul.luminescence.shading

import me.paul.luminescence.geometry.{Geometry, Ray3D}

class RayHit(val ray: Ray3D, val geometry: Geometry, val time: Double) extends Ordered[RayHit] {

    def compare(y: RayHit): Int = {
        if(this.time < y.time) {
            -1
        } else if (this.time > y.time) {
            1
        } else {
            0
        }
    }

}

object RayHit {

    def apply(r: Ray3D, g: Geometry, t: Double): RayHit = new RayHit(r, g, t)

}
