package me.paul.luminescence

import java.io.File

import javafx.scene.paint.Color

object Parameters {

    val VERSION: String = "0.5"

    val IMAGE_HEIGHT: Int = 1000
    val IMAGE_WIDTH: Int = 1000

    val BACKGROUND_COLOR: Color = Color.BLACK

    val GAMMA_CORRECTION: Int = 2

    val SAMPLE_COUNT: Int = 20
    val BOUNCE_COUNT: Int = 3

    val TRIANGLE_INTERSECTION_EPSILON = 0.0000001
    val TIME_MINIMUM: Double = 0.0000001
    val TIME_MAXIMUM: Double = Double.MaxValue

    val IMAGE_FILE_FORMAT: String = "png"
    val FILENAME: String = s"$VERSION-${IMAGE_WIDTH}x$IMAGE_HEIGHT-${SAMPLE_COUNT}samples"
    val FILEPATH: File = new File(new File(Parameters.getClass.getProtectionDomain.getCodeSource.getLocation.getFile).getParent + "/images/")

}
