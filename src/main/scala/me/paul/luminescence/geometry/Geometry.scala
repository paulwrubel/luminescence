package me.paul.luminescence.geometry

import me.paul.luminescence.shading.{Material, RayHit}

trait Geometry {

    val material: Material

    def intersects(ray: Ray3D): Boolean
    def intersections(ray: Ray3D): List[RayHit]

    def normalAt(point: Point3D): Vector3D

}
