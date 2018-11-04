package me.paul.luminescence

import java.io.File

object Parameters {

    val VERSION: String = "0.1"

    val IMAGE_WIDTH: Int = 300
    val IMAGE_HEIGHT: Int = 300
    val IMAGE_FILE_FORMAT: String = "png"

    val FILENAME: String = s"$VERSION-${IMAGE_WIDTH}x$IMAGE_HEIGHT"
    val FILEPATH: File = new File(new File(Parameters.getClass.getProtectionDomain.getCodeSource.getLocation.getFile).getParent)

}
