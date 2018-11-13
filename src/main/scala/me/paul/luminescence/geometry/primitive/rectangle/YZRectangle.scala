package me.paul.luminescence.geometry.primitive.rectangle

import me.paul.luminescence.geometry.{Geometry, Point3D, Ray3D, Vector3D}
import me.paul.luminescence.shading.RayHit
import me.paul.luminescence.shading.material.Material

class YZRectangle(_y0: Double, _y1: Double, _z0: Double, _z1: Double, x: Double,
                  val material: Material, isCulled: Boolean, override val reverseNormals: Boolean) extends Geometry {

    private val y0 = math.min(_y0, _y1)
    private val y1 = math.max(_y0, _y1)
    private val z0 = math.min(_z0, _z1)
    private val z1 = math.max(_z0, _z1)

    override def intersections(ray: Ray3D, tMin: Double, tMax: Double): Option[RayHit] = {

        // Ray is coming from behind rectangle
        if (isCulled && (ray.direction dot normal) > 0) {
            return None
        }

        // Ray is parallel to plane
        if (ray.direction.x == 0) {
            return None
        }

        val t = (x - ray.origin.x) / ray.direction.x

        if (t < tMin || t > tMax) {
            return None
        }

        val y = ray.origin.y + (t * ray.direction.y)
        val z = ray.origin.z + (t * ray.direction.z)

        // plane intersection not within rectangle
        if (y < y0 || y > y1 || z < z0 || z > z1) {
            return None
        }

        Some(RayHit(ray, normal, t, material))

    }

    def normal: Vector3D = {
        if (reverseNormals) {
            Vector3D(-1, 0, 0)
        } else {
            Vector3D(1, 0, 0)
        }
    }

}

object YZRectangle {

    def apply(y0: Double, y1: Double, z0: Double, z1: Double, x: Double, m: Material, ic: Boolean, rn: Boolean): YZRectangle = new YZRectangle(y0, y1, z0, z1, x, m, ic, rn)
    def apply(y0: Double, y1: Double, z0: Double, z1: Double, x: Double, m: Material, rn: Boolean): YZRectangle = new YZRectangle(y0, y1, z0, z1, x, m, false, rn)
    def apply(y0: Double, y1: Double, z0: Double, z1: Double, x: Double, m: Material): YZRectangle = new YZRectangle(y0, y1, z0, z1, x, m, false, false)

}
