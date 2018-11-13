package me.paul.luminescence.geometry.primitive

import me.paul.luminescence.geometry.primitive.rectangle.{XYRectangle, XZRectangle, YZRectangle}
import me.paul.luminescence.geometry.{Geometry, Point3D, Ray3D, Vector3D}
import me.paul.luminescence.shading.RayHit
import me.paul.luminescence.shading.material.Material

class Rectangle(_p0: Point3D, _p1: Point3D, val material: Material, isCulled: Boolean, override val reverseNormals: Boolean) extends Geometry {

    val rect: Geometry =
        if (_p0.x == _p1.x) {
            // lies on YZ plane
            YZRectangle(_p0.y, _p1.y, _p0.z, _p1.z, _p0.x, material, isCulled, reverseNormals)

        } else if (_p0.y == _p1.y) {
            // lies on XZ plane
            XZRectangle(_p0.x, _p1.x, _p0.z, _p1.z, _p0.y, material, isCulled, reverseNormals)

        } else if (_p0.z == _p1.z) {
            // lies on XY plane
            XYRectangle(_p0.x, _p1.x, _p0.y, _p1.y, _p0.z, material, isCulled, reverseNormals)

        } else {
            throw new IllegalArgumentException("Points do not lie on on axis-aligned plane")
        }

    override def intersections(ray: Ray3D, tMin: Double, tMax: Double): Option[RayHit] = {
        rect.intersections(ray, tMin, tMax)
    }

}

object Rectangle {

    def apply(_p0: Point3D, _p1: Point3D, m: Material, ic: Boolean, rn: Boolean): Rectangle = new Rectangle(_p0, _p1, m, ic, rn)
    def apply(_p0: Point3D, _p1: Point3D, m: Material, rn: Boolean): Rectangle = new Rectangle(_p0, _p1, m, false, rn)
    def apply(_p0: Point3D, _p1: Point3D, m: Material): Rectangle = new Rectangle(_p0, _p1, m, false, false)

}
