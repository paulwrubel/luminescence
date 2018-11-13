package me.paul.luminescence.shading.material
import me.paul.luminescence.geometry.{Point3D, Ray3D, Vector3D}
import me.paul.luminescence.shading.RayHit

class LambertianMaterial(val reflectance: Vector3D, val emittance: Vector3D) extends Material {

    override def scatter(hit: RayHit): Option[Ray3D] = {
        val hitPoint = hit.ray.pointAt(hit.time)
        val target: Point3D = hitPoint + hit.normalAtHit + Vector3D.randomInUnitSphere
        Some(Ray3D(hitPoint, hitPoint to target))
    }

}

object LambertianMaterial {

    def apply(r: Vector3D, e: Vector3D): LambertianMaterial = new LambertianMaterial(r, e)
    def apply(r: Vector3D): LambertianMaterial = new LambertianMaterial(r, Vector3D.ZERO)

    def light(e: Vector3D): LambertianMaterial = LambertianMaterial(Vector3D.ZERO, e)

    val WHITE_LIGHT = LambertianMaterial(Vector3D.ZERO, Vector3D(1, 1, 1))

}
