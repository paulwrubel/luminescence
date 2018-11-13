package me.paul.luminescence.geometry.primitive

import me.paul.luminescence.geometry.primitive.rectangle._
import me.paul.luminescence.geometry.{Geometry, Point3D, Ray3D, Vector3D}
import me.paul.luminescence.shading.RayHit
import me.paul.luminescence.shading.material.Material

class Box(_p0: Point3D, _p1: Point3D, val material: Material) extends Geometry {

    private val point0 = _p0.minComponents(_p1)
    private val point1 = _p0.maxComponents(_p1)

    private val xy0 = XYRectangle(point0.x, point1.x, point0.y, point1.y, point0.z, material, rn = true)
    private val xy1 = XYRectangle(point0.x, point1.x, point0.y, point1.y, point1.z, material)
    private val xz0 = XZRectangle(point0.x, point1.x, point0.z, point1.z, point0.y, material, rn = true)
    private val xz1 = XZRectangle(point0.x, point1.x, point0.z, point1.z, point1.y, material)
    private val yz0 = YZRectangle(point0.y, point1.y, point0.z, point1.z, point0.x, material, rn = true)
    private val yz1 = YZRectangle(point0.y, point1.y, point0.z, point1.z, point1.x, material)

    val rectangles = List(xy0, xy1, xz0, xz1, yz0, yz1)

    override def intersections(ray: Ray3D, tMin: Double, tMax: Double): Option[RayHit] = {

        val collisions: List[RayHit] = rectangles.flatMap(g => g.intersections(ray, tMin, tMax))

        if (collisions.isEmpty) {
            return None
        }

        Some(collisions.min)
    }

}

object Box {

    def apply(_p0: Point3D, _p1: Point3D, m: Material): Box = new Box(_p0, _p1, m)

}
