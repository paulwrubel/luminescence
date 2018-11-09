package me.paul.luminescence.geometry

import me.paul.luminescence.Parameters
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
        val ab = a to b
        val ac = a to c
        val h = ray.direction cross ac
        val aprime = ab dot h
        if (aprime > -Parameters.TRIANGLE_INTERSECTION_EPSILON && aprime < Parameters.TRIANGLE_INTERSECTION_EPSILON)
            return List.empty    // This ray is parallel to this triangle.
        val f = 1.0 / aprime
        val s = ray.start - a
        val u = f * (s dot h)
        if (u < 0.0 || u > 1.0)
            return List.empty
        val q = s cross ab
        val v = f * (ray.direction dot q)
        if (v < 0.0 || u + v > 1.0)
            return List.empty
        // At this stage we can compute t to find out where the intersection point is on the line.
        val t = f * (ac dot q)
        if (t > Parameters.TRIANGLE_INTERSECTION_EPSILON) // ray intersection
        {
            List(RayHit(ray, this, t))
        }
        else // This means that there is a line intersection but not a ray intersection.
            List.empty
    }

//    override def intersections(ray: Ray3D): List[RayHit] = {
//        if ((normal dot ray.direction) == 0) {
//            List.empty
//        } else {
//            val d = normal dot a.toVector3D
//            val t = (d - (normal dot ray.start.toVector3D)) / (normal dot ray.direction)
//
//            val q = ray.pointAt(t)
//
//            if ((((a to b) cross (a to q)) dot normal) >= 0
//                    && (((b to c) cross (b to q)) dot normal) >= 0
//                    && (((c to a) cross (c to q)) dot normal) >= 0) {
//                List(RayHit(ray, this, t))
//            } else {
//                List.empty
//            }
//        }
//    }

    override def normalAt(point: Point3D): Vector3D = normal

}

object Triangle {

    def apply(a: Point3D, b: Point3D, c: Point3D, m: Material): Triangle = new Triangle(a, b, c, m)

}
