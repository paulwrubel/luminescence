package me.paul.luminescence.geometry.primitive.rectangle

import me.paul.luminescence.geometry.{Geometry, Point3D, Ray3D, Vector3D}
import me.paul.luminescence.shading.RayHit
import me.paul.luminescence.shading.material.Material

class XYRectangle(_x0: Double, _x1: Double, _y0: Double, _y1: Double, z: Double,
                  val material: Material, isCulled: Boolean, override val reverseNormals: Boolean) extends Geometry {

    private val x0 = math.min(_x0, _x1)
    private val x1 = math.max(_x0, _x1)
    private val y0 = math.min(_y0, _y1)
    private val y1 = math.max(_y0, _y1)

    override def intersections(ray: Ray3D, tMin: Double, tMax: Double): Option[RayHit] = {

        // Ray is coming from behind rectangle
        if (isCulled && (ray.direction dot normal) > 0) {
            return None
        }

        // Ray is parallel to plane
        if (ray.direction.z == 0) {
            return None
        }

        val t = (z - ray.origin.z) / ray.direction.z

        if (t < tMin || t > tMax) {
            return None
        }

        val x = ray.origin.x + (t * ray.direction.x)
        val y = ray.origin.y + (t * ray.direction.y)

        // plane intersection not within rectangle
        if (x < x0 || x > x1 || y < y0 || y > y1) {
            return None
        }

        Some(RayHit(ray, normal, t, material))

    }

    def normal: Vector3D = {
        if (reverseNormals) {
            Vector3D(0, 0, -1)
        } else {
            Vector3D(0, 0, 1)
        }
    }

}

object XYRectangle {

    def apply(x0: Double, x1: Double, y0: Double, y1: Double, z: Double, m: Material, ic: Boolean, rn: Boolean): XYRectangle = new XYRectangle(x0, x1, y0, y1, z, m, ic, rn)
    def apply(x0: Double, x1: Double, y0: Double, y1: Double, z: Double, m: Material, rn: Boolean): XYRectangle = new XYRectangle(x0, x1, y0, y1, z, m, false, rn)
    def apply(x0: Double, x1: Double, y0: Double, y1: Double, z: Double, m: Material): XYRectangle = new XYRectangle(x0, x1, y0, y1, z, m, false, false)

}
