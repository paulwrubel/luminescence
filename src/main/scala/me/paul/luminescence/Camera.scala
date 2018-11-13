package me.paul.luminescence

import me.paul.luminescence.geometry.{Point3D, Ray3D, Vector3D}

class Camera(val eyeLocation: Point3D, lookAt: Point3D, up: Vector3D, verticalFOV: Double, aspectRatio: Double, aperture: Double, focusDist: Double) {

    private val lensRadius = aperture / 2.0

    private val theta: Double = verticalFOV * math.Pi / 180
    private val halfHeight: Double = math.tan(theta / 2)
    private val halfWidth: Double = aspectRatio * halfHeight

    private val w: Vector3D = (lookAt to eyeLocation).normalize
    private val u: Vector3D = up cross w
    private val v: Vector3D = w cross u

    val southwest: Point3D = eyeLocation - (u * halfWidth * focusDist) - (v * halfHeight * focusDist) - (w * focusDist)
    val horizontal: Vector3D = u * 2 * halfWidth * focusDist
    val vertical: Vector3D = v * 2 * halfHeight * focusDist

    def getRay(x: Double, y: Double): Ray3D = {
        val randomOnLens = Vector3D.randomOnUnitDisk * lensRadius
        val offset = (u * randomOnLens.x) + (v * randomOnLens.y)
        Ray3D(eyeLocation + offset, ((southwest + (horizontal * x) + (vertical * y)) - eyeLocation) - offset)
    }

}

object Camera {

    def apply(eye: Point3D, lookAt: Point3D, up: Vector3D, vfov: Double, ar: Double, ap: Double, fd: Double): Camera =
        new Camera(eye, lookAt, up, vfov, ar, ap, fd)

    def apply(eye: Point3D, lookAt: Point3D, up: Vector3D, vfov: Double, ar: Double): Camera =
        new Camera(eye, lookAt, up, vfov, ar, 0.0, 1.0)

}
