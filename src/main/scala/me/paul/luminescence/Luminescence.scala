package me.paul.luminescence

import java.io.{File, IOException}

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import javax.imageio.ImageIO
import me.paul.luminescence.LoopUtil._
import me.paul.luminescence.geometry._
import me.paul.luminescence.shading.{ShadedColor, ShadingUtil}

object Luminescence {

    def main(args: Array[String]): Unit = {
        val image = getImage

        val pixelWriter = image.getPixelWriter

        val viewportCenterLocation = Point3D(10, 10, 0)

        val eyeLocation = viewportCenterLocation + Vector3D(0, 0, 5)

        val lightLocation = viewportCenterLocation + Vector3D(-10, 10, 0)

        val nw = viewportCenterLocation + Vector3D(-5, 2.5, 0)
        val se = viewportCenterLocation + Vector3D(5, -2.5, 0)

        val planeWidth: Double = (nw to se).x.abs
        val planeHeight: Double = (nw to se).y.abs

        val geometryList: List[Geometry] = List(
            Sphere(viewportCenterLocation + Vector3D(5, 0, -20), 1, ShadedColor(Color.RED)),
            Sphere(viewportCenterLocation + Vector3D(2, -2, -12), 2, ShadedColor(Color.BLUE)),
            Sphere(viewportCenterLocation + Vector3D(0, 0, -9), 1, ShadedColor(Color.AQUA)),
            Sphere(viewportCenterLocation + Vector3D(1, 0, -30), 5, ShadedColor(Color.GREEN))
        )


        (0 until Parameters.IMAGE_WIDTH).foreach(x => {
            ((Parameters.IMAGE_HEIGHT - 1) to 0 by -1).foreach(y => {

                val planeLocation =
                    nw + (Vector3D.RIGHT * x * planeWidth / Parameters.IMAGE_WIDTH) + (-Vector3D.UP * y * planeHeight / Parameters.IMAGE_HEIGHT) +
                            Vector3D(planeWidth / Parameters.IMAGE_WIDTH / 2, -planeHeight / Parameters.IMAGE_HEIGHT / 2, 0)

                val rayIntoScene = Ray3D(eyeLocation, eyeLocation to planeLocation)

                val c: ShadedColor =
                    getClosestCollision(rayIntoScene, geometryList) match {
                        case Some(t) if t._1.isInstanceOf[Sphere] =>
                            val s = t._1.asInstanceOf[Sphere]
                            val spherePoint = rayIntoScene.pointAt(t._2)
                            val shadowRay = Ray3D(spherePoint, spherePoint to lightLocation)

                            val ambientColor = ShadingUtil.ambient(s.color, 0.1)

                            getClosestCollision(shadowRay, geometryList.filter(_ != s)) match {
                                case Some(_)=>
                                    println(s"putting a shadow on $s")
                                    ambientColor
                                case _ =>
                                    val diffuseColor = ShadingUtil.diffuse(s.color, s.center to spherePoint, spherePoint to lightLocation)
                                    diffuseColor + ambientColor
                            }
                        case _ =>
                            Parameters.BACKGROUND_COLOR
                    }

                pixelWriter.setColor(x, y, c.color)
            })
        })

        // image filling occurs here

        writeImageToFile(image, getFile)
    }

    def getClosestCollision(ray: Ray3D, geometry: List[Geometry]): Option[(Geometry, Double)] = {
        val possibleCollisions: List[Option[(Geometry, Double)]] = geometry.map{
            case s: Sphere =>
                ray intersections s match {
                    case Some(intersectionPoints) =>
                        if (intersectionPoints._1 < 0 && intersectionPoints._2 < 0) {
                            None
                        } else if (intersectionPoints._1 < 0) {
                            Some((s, intersectionPoints._2))
                        } else if (intersectionPoints._2 < 0) {
                            Some((s, intersectionPoints._1))
                        } else {
                            Some((s, math.min(intersectionPoints._1, intersectionPoints._2)))
                        }
                    case None =>
                        None
                }
            case _ =>
                None
        }

        val collisions: List[(Geometry, Double)] = possibleCollisions.filter(_.isDefined).map(_.get)

        if (collisions.isEmpty) {
            None
        } else {
            Some(collisions.reduce((last: (Geometry, Double), cur: (Geometry, Double)) => {
                if (last._2 <= cur._2) {
                    last
                } else {
                    cur
                }
            }))
        }
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
