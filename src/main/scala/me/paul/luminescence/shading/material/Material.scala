package me.paul.luminescence.shading.material

import me.paul.luminescence.geometry.{Ray3D, Vector3D}
import me.paul.luminescence.shading.RayHit

trait Material {

    val reflectance: Vector3D
    val emittance: Vector3D

    def isSpecular: Boolean = false

    def scatter(hit: RayHit): Option[Ray3D]

}