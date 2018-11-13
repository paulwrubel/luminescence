package me.paul.luminescence.geometry.primitive.rectangle

import me.paul.luminescence.geometry.{Geometry, Point3D, Ray3D, Vector3D}
import me.paul.luminescence.shading.RayHit
import me.paul.luminescence.shading.material.Material

class XZRectangle(_x0: Double, _x1: Double, _z0: Double, _z1: Double, y: Double,
                  val material: Material, isCulled: Boolean, override val reverseNormals: Boolean) extends Geometry {

    private val x0 = math.min(_x0, _x1)
    private val x1 = math.max(_x0, _x1)
    private val z0 = math.min(_z0, _z1)
    private val z1 = math.max(_z0, _z1)

    override def intersections(ray: Ray3D, tMin: Double, tMax: Double): Option[RayHit] = {

        // Ray is coming from behind rectangle
        if (isCulled && (ray.direction dot normal) > 0) {
            return None
        }

        // Ray is parallel to plane
        if (ray.direction.y == 0) {
            return None
        }

        val t = (y - ray.origin.y) / ray.direction.y

        if (t < tMin || t > tMax) {
            return None
        }

        val x = ray.origin.x + (t * ray.direction.x)
        val z = ray.origin.z + (t * ray.direction.z)

        // plane intersection not within rectangle
        if (x < x0 || x > x1 || z < z0 || z > z1) {
            return None
        }

        Some(RayHit(ray, normal, t, material))

    }

    def normal: Vector3D = {
        if (reverseNormals) {
            Vector3D(0, -1, 0)
        } else {
            Vector3D(0, 1, 0)
        }
    }

}

object XZRectangle {

    def apply(x0: Double, x1: Double, z0: Double, z1: Double, y: Double, m: Material, ic: Boolean, rn: Boolean): XZRectangle = new XZRectangle(x0, x1, z0, z1, y, m, ic, rn)
    def apply(x0: Double, x1: Double, z0: Double, z1: Double, y: Double, m: Material, rn: Boolean): XZRectangle = new XZRectangle(x0, x1, z0, z1, y, m, false, rn)
    def apply(x0: Double, x1: Double, z0: Double, z1: Double, y: Double, m: Material): XZRectangle = new XZRectangle(x0, x1, z0, z1, y, m, false, false)

}
