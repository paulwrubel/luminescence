package me.paul.luminescence.shading.material

import me.paul.luminescence.geometry.{Point3D, Ray3D, Vector3D}
import me.paul.luminescence.shading.{RayHit, ShadingUtil}

class MetalMaterial(f: Double, val reflectance: Vector3D, val emittance: Vector3D) extends Material {
    private val fuzziness = ShadingUtil.clamp(f, 0.0, 1.0)

    override def isSpecular: Boolean = true

    override def scatter(hit: RayHit): Option[Ray3D] = {
        val hitPoint = hit.ray.pointAt(hit.time)
        val normal = hit.normalAtHit

        val reflectionVector = hit.ray.direction.normalize.reflectAround(normal)
        val reflectionRay = Ray3D(hitPoint, reflectionVector + (Vector3D.randomInUnitSphere * fuzziness))

        if ((reflectionVector dot normal) > 0) {
            Some(reflectionRay)
        } else {
            None
        }
    }

}

object MetalMaterial {

    def apply(f: Double, r: Vector3D, e: Vector3D): MetalMaterial = new MetalMaterial(f, r, e)
    def apply(f: Double, r: Vector3D): MetalMaterial = new MetalMaterial(f, r, Vector3D.ZERO)
    def apply(f: Double): MetalMaterial = new MetalMaterial(f, Vector3D(1), Vector3D.ZERO)

}



