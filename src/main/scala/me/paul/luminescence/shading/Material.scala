package me.paul.luminescence.shading

import me.paul.luminescence.geometry.Vector3D

class Material(val reflectance: Vector3D, val emittance: Vector3D) {

}

object Material {

    def apply(r: Vector3D, e: Vector3D): Material = new Material(r, e)
    def light(e: Vector3D): Material = Material(Vector3D.ZERO, e)

    val WHITE_LIGHT = Material(Vector3D.ZERO, Vector3D(1, 1, 1))

}