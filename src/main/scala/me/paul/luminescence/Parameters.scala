package me.paul.luminescence

import java.io.File

import javafx.scene.paint.Color
import me.paul.luminescence.shading.ShadedColor

object Parameters {

    val VERSION: String = "0.1"

    val IMAGE_WIDTH: Int = 1600
    val IMAGE_HEIGHT: Int = 800
    val IMAGE_FILE_FORMAT: String = "png"

    val FILENAME: String = s"$VERSION-${IMAGE_WIDTH}x$IMAGE_HEIGHT"
    val FILEPATH: File = new File(new File(Parameters.getClass.getProtectionDomain.getCodeSource.getLocation.getFile).getParent + "/images/")

    val BACKGROUND_COLOR: ShadedColor = ShadedColor(Color.TRANSPARENT)

    val AMBIENT_LEVEL: Double = 0.1
    val DIFFUSE_WEIGHT: Double = 1.0
    val SPECULAR_WEIGHT: Double = 1.0
    val SPECULAR_SHININESS: Int = 40

}
