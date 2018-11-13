package me.paul.luminescence.geometry

class Point3D(val x: Double, val y: Double, val z: Double) {

    def this(xyz: (Double, Double, Double)) = {
        this(xyz._1, xyz._2, xyz._3)
    }

    def to(p: Point3D): Vector3D = {
        p - this
    }

    def from(p: Point3D): Vector3D = {
        this - p
    }

    def minComponents(p: Point3D): Point3D = {
        Point3D(math.min(this.x, p.x), math.min(this.y, p.y), math.min(this.z, p.z))
    }

    def maxComponents(p: Point3D): Point3D = {
        Point3D(math.max(this.x, p.x), math.max(this.y, p.y), math.max(this.z, p.z))
    }

    def toVector3D: Vector3D = {
        Vector3D(x, y, z)
    }

    def +(v: Vector3D): Point3D = {
        Point3D(x + v.x, y + v.y, z + v.z)
    }

    def -(v: Vector3D): Point3D = {
        this + -v
    }

    def -(p: Point3D): Vector3D = {
        toVector3D - p.toVector3D
    }

    override def toString: String = {
        s"Point3D($x, $y, $z)"
    }

}

object Point3D {

    def apply(x: Double, y: Double, z: Double): Point3D = new Point3D(x, y, z)
    val ORIGIN: Point3D = Point3D(0.0, 0.0, 0.0)

}
