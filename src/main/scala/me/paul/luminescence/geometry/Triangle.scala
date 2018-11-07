package me.paul.luminescence.geometry

import me.paul.luminescence.shading.{Material, RayHit}

class Triangle(val a: Point3D, val b: Point3D, val c: Point3D, val material: Material) extends Geometry {

    def normal: Vector3D = {
        ((a to b) cross (a to c)).normalize
    }

    override def intersects(ray: Ray3D): Boolean = {
        if ((normal dot ray.direction) == 0) {
            false
        } else {
            val d = normal dot a.toVector3D
            val t = (d - (normal dot ray.start.toVector3D)) / (normal dot ray.direction)

            val q = ray.pointAt(t)

            if ((((a to b) cross (a to q)) dot normal) >= 0
                        && (((b to c) cross (b to q)) dot normal) >= 0
                        && (((c to a) cross (c to q)) dot normal) >= 0) {
                true
            } else {
                false
            }
        }
    }

    override def intersections(ray: Ray3D): List[RayHit] = {
        if ((normal dot ray.direction) == 0) {
            List.empty
        } else {
            val d = normal dot a.toVector3D
            val t = (d - (normal dot ray.start.toVector3D)) / (normal dot ray.direction)

            val q = ray.pointAt(t)

            if ((((a to b) cross (a to q)) dot normal) >= 0
                    && (((b to c) cross (b to q)) dot normal) >= 0
                    && (((c to a) cross (c to q)) dot normal) >= 0) {
                List(RayHit(ray, this, t))
            } else {
                List.empty
            }
        }
    }

    override def normalAt(point: Point3D): Vector3D = normal

}

object Triangle {

    def apply(a: Point3D, b: Point3D, c: Point3D, m: Material): Triangle = new Triangle(a, b, c, m)

}
