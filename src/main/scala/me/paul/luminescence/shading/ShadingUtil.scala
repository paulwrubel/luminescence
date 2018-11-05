package me.paul.luminescence.shading

import me.paul.luminescence.geometry.Vector3D

object ShadingUtil {

    def diffuse(shadedColor: ShadedColor, normal: Vector3D, toLight: Vector3D): ShadedColor = {
        shadedColor * clamp(normal.normalize dot toLight.normalize, 0.0, 1.0)
    }

    def ambient(shadedColor: ShadedColor, ambient: Double): ShadedColor = {
        shadedColor * ambient
    }

    def clamp(value: Double, min: Double, max: Double): Double = {
        if (value < min) {
            min
        } else if (value > max) {
            max
        } else {
            value
        }
    }

}
