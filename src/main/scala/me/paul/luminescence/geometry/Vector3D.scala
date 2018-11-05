package me.paul.luminescence.geometry

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
        Vector3D((y * v.z) - (z * v.y), (x * v.z) - (z * v.x), (x * v.y) - (y * v.x))
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

    def +(v: Vector3D): Vector3D = {
        Vector3D(x + v.x, y + v.y, z + v.z)
    }

    def -(v: Vector3D): Vector3D = {
        Vector3D(x - v.x, y - v.y, z - v.z)
    }

    def *(s: Double): Vector3D = {
        Vector3D(x * s, y * s, z * s)
    }

    def /(s: Double): Vector3D = {
        Vector3D(x / s, y / s, z / s)
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
    
    val RIGHT: Vector3D = new Vector3D(1.0, 0.0, 0.0)
    val UP: Vector3D = new Vector3D(0.0, 1.0, 0.0)
    val FORWARD: Vector3D = new Vector3D(0.0, 0.0, -1.0)

    val ZERO: Vector3D = new Vector3D(0.0, 0.0, 0.0)
}
