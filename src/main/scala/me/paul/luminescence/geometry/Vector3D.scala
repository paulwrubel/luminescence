package me.paul.luminescence.geometry

import javafx.scene.paint.Color
import me.paul.luminescence.RandomUtil

class Vector3D(val x: Double, val y: Double, val z: Double) {

    def this(xyz: (Double, Double, Double)) = {
        this(xyz._1, xyz._2, xyz._3)
    }

    def magnitude: Double = {
        math.sqrt( (x * x) + (y * y) + (z * z) )
    }

    def normalize: Vector3D = {
        this / magnitude
    }

    def dot(v: Vector3D): Double = {
        (x * v.x) + (y * v.y) + (z * v.z)
    }

    def cross(v: Vector3D): Vector3D = {
        Vector3D((y * v.z) - (z * v.y), (z * v.x) - (x * v.z), (x * v.y) - (y * v.x))
    }

    def angleRadians(v: Vector3D): Double = {
        math.acos(dot(v) / (magnitude * v.magnitude))
    }

    def angleDegrees(v: Vector3D): Double = {
        math.toDegrees(angleRadians(v))
    }

    def toPoint3D: Point3D = {
        Point3D(x, y, z)
    }

    def toColor: Color = {
        Color.color(x, y, z)
    }

    def perpendicular: Vector3D = {
        if (x.abs > y.abs) {
            Vector3D(z, 0, -x)
        } else {
            Vector3D(0, -z, y)
        }
    }

    def inHemisphereOf: Vector3D = {

        val r1 = RandomUtil.randomUpTo(1)
        val r2 = RandomUtil.randomUpTo(1)

        val x = RandomUtil.randomBetween(-1, 1)
        val y = RandomUtil.randomBetween(-1, 1)
        val z = RandomUtil.randomBetween(-1, 1)

        val sample = Vector3D(x, y, z)

        if ((sample dot this) < 0) {
            -sample
        } else {
            sample
        }
    }

    def inHemisphereOfTrue: Vector3D = {

        val up = this.normalize
        val right = perpendicular.normalize
        val forward = up cross right

        val r1 = RandomUtil.randomUpTo(1)
        val r2 = RandomUtil.randomUpTo(1)

        val y = r1

        val theta = math.acos(1 - y * y)
        val phi = 2 * math.Pi * r2

        val x = math.sqrt(theta) * math.cos(phi)
        val z = math.sqrt(theta) * math.sin(phi)

        val sample = Vector3D(x, y, z)

        val transformedSample = Vector3D(
            sample.x * forward.x + sample.y * up.x + sample.z * right.x,
            sample.x * forward.y + sample.y * up.y + sample.z * right.y,
            sample.x * forward.z + sample.y * up.z + sample.z * right.z
        )

        transformedSample

    }

    def +(v: Vector3D): Vector3D = {
        Vector3D(x + v.x, y + v.y, z + v.z)
    }

    def -(v: Vector3D): Vector3D = {
        Vector3D(x - v.x, y - v.y, z - v.z)
    }

    def *(s: Double): Vector3D = {
        Vector3D(x * s, y * s, z * s)
    }

    def *(v: Vector3D): Vector3D = {
        Vector3D(x * v.x, y * v.y, z * v.z)
    }

    def /(s: Double): Vector3D = {
        Vector3D(x / s, y / s, z / s)
    }

    def /(v: Vector3D): Vector3D = {
        Vector3D(x / v.x, y / v.y, z / v.z)
    }

    def unary_- : Vector3D = {
        Vector3D.ZERO - this
    }

    def unary_! : Boolean = {
        !this.equals(Vector3D.ZERO)
    }

    override def toString: String = {
        s"Vector3D($x, $y, $z)"
    }

    override def equals(obj: scala.Any): Boolean = {
        obj match {
            case v: Vector3D =>
                v.x == x && v.y == y && v.z == z
            case _ =>
                false
        }
    }

}

object Vector3D {

    def apply(x: Double, y: Double, z: Double): Vector3D = new Vector3D(x, y, z)
    def apply(c: Color): Vector3D = new Vector3D(c.getRed, c.getGreen, c.getBlue)

    val RIGHT: Vector3D = new Vector3D(1.0, 0.0, 0.0)
    val UP: Vector3D = new Vector3D(0.0, 1.0, 0.0)
    val FORWARD: Vector3D = new Vector3D(0.0, 0.0, -1.0)

    val ZERO: Vector3D = new Vector3D(0.0, 0.0, 0.0)
}
