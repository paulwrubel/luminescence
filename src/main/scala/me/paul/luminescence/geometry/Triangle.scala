package me.paul.luminescence.geometry

import me.paul.luminescence.Parameters
import me.paul.luminescence.shading.RayHit
import me.paul.luminescence.shading.material.Material

class Triangle(val a: Point3D, val b: Point3D, val c: Point3D, val material: Material) extends Geometry {

    def normal: Vector3D = {
        ((a to b) cross (a to c)).normalize
    }

    override def intersections(ray: Ray3D, min: Double, max: Double): Option[RayHit] = {
        intersectionsMollerTrumbore(ray, min, max)
    }

    def intersectionsCramer(ray: Ray3D, min: Double, max: Double): Option[RayHit] = {

        if ((ray.direction dot normal) >= 0) {
            return None
        }

        val A = a.x - b.x
        val B = a.y - b.y
        val C = a.z - b.z
        val D = a.x - c.x
        val E = a.y - c.y
        val F = a.z - c.z
        val G = ray.direction.x
        val H = ray.direction.y
        val I = ray.direction.z
        val J = a.x - ray.start.x
        val K = a.y - ray.start.y
        val L = a.z - ray.start.z

        val M = (A * ((E * I) - (H * F))) + (B * ((G * F) - (D * I))) + (C * ((D * H) - (E * G)))

        val t = -((F * ((A * K) - (J * B))) + (E * ((J * C) - (A * L))) + (D * ((B * L) - (K * C)))) / M

        if (t >= min && t <= max) {
            val gamma = ((I * ((A * K) - (J * B))) + (H * ((J * C) - (A * L))) + (G * ((B * L) - (K * C)))) / M
            if (gamma >= 0 && gamma <= 1) {
                val beta = ((J * ((E * I) - (H * F))) + (K * ((G * F) - (D * I))) + (L * ((D * H) - (E * G)))) / M
                if (beta >= 0 && beta <= 1 - gamma) {
                    return Some(RayHit(ray, this, t))
                }
            }
        }
        None
    }

    // vector optimization of Cramer method
    def intersectionsMollerTrumbore(ray: Ray3D, min: Double, max: Double): Option[RayHit] = {
        val ab = a to b
        val ac = a to c
        val pVector = ray.direction cross ac
        val determinant = ab dot pVector
        if (determinant < Parameters.TRIANGLE_INTERSECTION_EPSILON) {
            // This ray is parallel to this triangle or back-facing.
            return None
        }

        val inverseDeterminant = 1.0 / determinant

        val tVector = a to ray.start
        val u = inverseDeterminant * (tVector dot pVector)
        if (u < 0.0 || u > 1.0) {
            return None
        }

        val qVector = tVector cross ab
        val v = inverseDeterminant * (ray.direction dot qVector)
        if (v < 0.0 || u + v > 1.0) {
            return None
        }

        // At this stage we can compute t to find out where the intersection point is on the line.
        val t = inverseDeterminant * (ac dot qVector)
        if (t >= min && t <= max) {
            // ray intersection
            Some(RayHit(ray, this, t))
        } else {
            None
        }

    }

    // cross method
    def intersectionsCross(ray: Ray3D, min: Double, max: Double): Option[RayHit] = {
        if ((normal dot ray.direction) == 0) {
            None
        } else {
            val d = normal dot a.toVector3D
            val t = (d - (normal dot ray.start.toVector3D)) / (normal dot ray.direction)

            val q = ray.pointAt(t)

            if ((((a to b) cross (a to q)) dot normal) >= 0
                    && (((b to c) cross (b to q)) dot normal) >= 0
                    && (((c to a) cross (c to q)) dot normal) >= 0) {
                Some(RayHit(ray, this, t))
            } else {
                None
            }
        }
    }

    override def normalAt(point: Point3D): Vector3D = normal

}

object Triangle {

    def apply(a: Point3D, b: Point3D, c: Point3D, m: Material): Triangle = new Triangle(a, b, c, m)

}
