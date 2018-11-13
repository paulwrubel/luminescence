package me.paul.luminescence.geometry.primitive

import me.paul.luminescence.geometry.{Geometry, Ray3D}
import me.paul.luminescence.shading.RayHit
import me.paul.luminescence.shading.material.Material

class ReverseNormalss(g: Geometry) extends Geometry {
    override val material: Material = g.material
    override def intersections(ray: Ray3D, tMin: Double, tMax: Double): Option[RayHit] = {
        val rhOption = g.intersections(ray, tMin, tMax)
        if (rhOption.isDefined) {
            val rh = rhOption.get
            Some(RayHit(rh, -rh.normalAtHit))
        } else {
            None
        }
    }
}

object ReverseNormals {

    def apply(g: Geometry): ReverseNormalss = new ReverseNormalss(g)

}
