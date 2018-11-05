package me.paul.luminescence

import java.io.{File, IOException}

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import javax.imageio.ImageIO
import me.paul.luminescence.LoopUtil._
import me.paul.luminescence.geometry.{Point3D, Ray3D, Sphere, Vector3D}

object Luminescence {

    def main(args: Array[String]): Unit = {
        val image = getImage

        val pixelWriter = image.getPixelWriter

        val viewportCenterLocation = Point3D(0, 0, 0)

        val eyeLocation = viewportCenterLocation + Vector3D(0, 0, 5)

        val nw = viewportCenterLocation + Vector3D(-3, 1.5, 0)
        val se = viewportCenterLocation + Vector3D(3, -1.5, 0)

        val planeWidth: Double = (nw to se).x.abs
        val planeHeight: Double = (nw to se).y.abs

        val ball = Sphere(viewportCenterLocation + Vector3D(0, 0, -10), 1, Color.BLACK)

        (0 until Parameters.IMAGE_WIDTH).foreach(x => {
            ((Parameters.IMAGE_HEIGHT - 1) to 0 by -1).foreach(y => {
                println("PING: " + ((Parameters.IMAGE_HEIGHT - 1) - y))
                val planeLocation =
                    nw + (Vector3D.RIGHT * x * planeWidth / Parameters.IMAGE_WIDTH) + (-Vector3D.UP * y * planeHeight / Parameters.IMAGE_HEIGHT)
                val rayIntoScene = Ray3D(eyeLocation, eyeLocation to planeLocation)

                val c: Color =
                    rayIntoScene intersections ball match {
                        case Some(i) =>
                            println("We've intersected!")
                            ball.color
                        case None =>
                            println("We've failed...")
                            println(s"Pixel = $x, $y")
                            println(s"Ray = $rayIntoScene")
                            println(s"planeLocation = $planeLocation")
                            Color.TRANSPARENT
                    }

                pixelWriter.setColor(x, y, c)
            })
        })

        // image filling occurs here

        writeImageToFile(image, getFile)
    }

    def getImage: WritableImage = new WritableImage(Parameters.IMAGE_WIDTH, Parameters.IMAGE_HEIGHT)

    def getFile: File = {
        val dir = Parameters.FILEPATH
        if ( !dir.exists && !dir.mkdirs() ) {
            throw new IOException("[ERROR]: COULD NOT CREATE NECESSARY DIRECTORIES")
        }

        var count = 0
        val exists = (f: File) => f.exists()
        doWhileYield[File](exists) {
            count += 1
            new File(Parameters.FILEPATH.getPath + "/" + Parameters.FILENAME + "_" + count + "." + Parameters.IMAGE_FILE_FORMAT)
        }
    }

    def writeImageToFile(image: WritableImage, file: File): Unit = {
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), Parameters.IMAGE_FILE_FORMAT, file)
        } catch {
            case ioe: IOException =>
                ioe.printStackTrace()
        }
    }
}
