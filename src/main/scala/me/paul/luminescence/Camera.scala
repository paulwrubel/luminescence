package me.paul.luminescence

import me.paul.luminescence.geometry.{Point3D, Ray3D, Vector3D}

class Camera(val eyeLocation: Point3D, lookAt: Point3D, up: Vector3D, verticalFOV: Double, aspectRatio: Double) {

    private val theta: Double = verticalFOV * math.Pi / 180
    private val halfHeight: Double = math.tan(theta / 2)
    private val halfWidth: Double = aspectRatio * halfHeight

    private val w: Vector3D = (lookAt to eyeLocation).normalize
    private val u: Vector3D = up cross w
    private val v: Vector3D = w cross u

    val southwest: Point3D = eyeLocation - (u * halfWidth) - (v * halfHeight) - w
    val horizontal: Vector3D = u * 2 * halfWidth
    val vertical: Vector3D = v * 2 * halfHeight

    def getRay(x: Double, y: Double): Ray3D = {
        Ray3D(eyeLocation, (southwest + (horizontal * x) + (vertical * y)) - eyeLocation)
    }

}

object Camera {

    def apply(eye: Point3D, lookAt: Point3D, up: Vector3D, vfov: Double, ar: Double): Camera = new Camera(eye, lookAt, up, vfov, ar)

}
