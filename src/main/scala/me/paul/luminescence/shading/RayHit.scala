package me.paul.luminescence.shading

import me.paul.luminescence.geometry.{Geometry, Ray3D, Vector3D}
import me.paul.luminescence.shading.material.Material

class RayHit(val ray: Ray3D, val normalAtHit: Vector3D, val time: Double, val material: Material) extends Ordered[RayHit] {

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

    def apply(r: Ray3D, n: Vector3D, t: Double, m: Material): RayHit = new RayHit(r, n, t, m)
    def apply(rh: RayHit, n: Vector3D): RayHit = new RayHit(rh.ray, n, rh.time, rh.material)

}
