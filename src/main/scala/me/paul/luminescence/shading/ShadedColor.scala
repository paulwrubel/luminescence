package me.paul.luminescence.shading

import javafx.scene.paint.Color

class ShadedColor(val color: Color) {

    def *(scalar: Double): ShadedColor = {
        ShadedColor(
            Color.color(
                ShadingUtil.clamp(color.getRed * scalar, 0.0, 1.0),
                ShadingUtil.clamp(color.getGreen * scalar, 0.0, 1.0),
                ShadingUtil.clamp(color.getBlue * scalar, 0.0, 1.0),
                color.getOpacity
            )
        )
    }

    // no range checking here!
    def +(scalar: Double): ShadedColor = {
        ShadedColor(
            Color.color(
                ShadingUtil.clamp(color.getRed + scalar, 0.0, 1.0),
                ShadingUtil.clamp(color.getGreen + scalar, 0.0, 1.0),
                ShadingUtil.clamp(color.getBlue + scalar, 0.0, 1.0),
                color.getOpacity
            )
        )
    }

    def +(other: ShadedColor): ShadedColor = {
        ShadedColor(
            Color.color(
                ShadingUtil.clamp(color.getRed + other.color.getRed, 0.0, 1.0),
                ShadingUtil.clamp(color.getGreen + other.color.getGreen, 0.0, 1.0),
                ShadingUtil.clamp(color.getBlue + other.color.getBlue, 0.0, 1.0),
                color.getOpacity
            )
        )
    }

}

object ShadedColor {

    def apply(c: Color): ShadedColor = new ShadedColor(c)

}