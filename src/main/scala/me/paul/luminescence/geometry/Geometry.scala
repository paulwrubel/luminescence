package me.paul.luminescence.geometry

import me.paul.luminescence.shading.RayHit
import me.paul.luminescence.shading.material.Material

trait Geometry {

    val material: Material
    val reverseNormals: Boolean = false

    def intersections(ray: Ray3D, tMin: Double, tMax: Double): Option[RayHit]

}