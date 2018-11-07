package me.paul.luminescence

import java.io.File

import javafx.scene.paint.Color

object Parameters {

    val VERSION: String = "0.1"

    val ASPECT_RATIO: Double = 2.0
    val IMAGE_HEIGHT: Int = 500
    val IMAGE_WIDTH: Int = (IMAGE_HEIGHT * ASPECT_RATIO).asInstanceOf[Int]
    val IMAGE_FILE_FORMAT: String = "png"

    val FILENAME: String = s"$VERSION-${IMAGE_WIDTH}x$IMAGE_HEIGHT"
    val FILEPATH: File = new File(new File(Parameters.getClass.getProtectionDomain.getCodeSource.getLocation.getFile).getParent + "/images/")

    val BACKGROUND_COLOR: Color = Color.BLACK

    val SAMPLE_COUNT: Int = 500
    val BOUNCE_COUNT: Int = 3
    val TIME_THRESHOLD: Double = 0.00001

}
