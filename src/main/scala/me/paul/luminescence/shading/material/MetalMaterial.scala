package me.paul.luminescence.shading.material

import me.paul.luminescence.geometry.{Point3D, Ray3D, Vector3D}
import me.paul.luminescence.shading.RayHit

class MetalMaterial(val reflectance: Vector3D, val emittance: Vector3D) extends Material {

    override def scatter(hit: RayHit): Option[Ray3D] = {
        val hitPoint = hit.ray.pointAt(hit.time)
        val normal = hit.geometry.normalAt(hitPoint)

        val reflectionVector = hit.ray.direction.normalize.reflectAround(normal)
        val reflectionRay = Ray3D(hitPoint, reflectionVector)

        if ((reflectionVector dot normal) > 0) {
            Some(reflectionRay)
        } else {
            None
        }
    }

}

object MetalMaterial {

    def apply(r: Vector3D, e: Vector3D): MetalMaterial = new MetalMaterial(r, e)
    def apply(r: Vector3D): MetalMaterial = new MetalMaterial(r, Vector3D.ZERO)

}



