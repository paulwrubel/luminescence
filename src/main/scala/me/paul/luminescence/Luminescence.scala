package me.paul.luminescence

import java.io.{File, IOException}

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import javax.imageio.ImageIO
import me.paul.luminescence.LoopUtil._
import me.paul.luminescence.geometry._
import me.paul.luminescence.shading.{Material, RayHit, ShadingUtil}

object Luminescence {

    def main(args: Array[String]): Unit = {
        val image = getImage

        val pixelWriter = image.getPixelWriter

        val viewportCenterLocation = Point3D(10, 10, 0)

        val eyeLocation = viewportCenterLocation + Vector3D(0, 0, 5)

        val light = Sphere(viewportCenterLocation + Vector3D(-5, 5, -10), 1, Material(Vector3D.ZERO, Vector3D(100, 100, 100)))

        val nw = viewportCenterLocation + Vector3D(-5, 2.5, 0)
        val se = viewportCenterLocation + Vector3D(5, -2.5, 0)

        val planeWidth: Double = (nw to se).x.abs
        val planeHeight: Double = (nw to se).y.abs

        val geometryList: List[Geometry] = List(
            light,
            Sphere(viewportCenterLocation + Vector3D(5, 0, -20), 1, Material.light(Vector3D(Color.RED))),
            Sphere(viewportCenterLocation + Vector3D(2, -2, -12), 2, Material(Vector3D(Color.BLUE), Vector3D.ZERO)),
            Sphere(viewportCenterLocation + Vector3D(0, 0, -9), 1, Material.light(Vector3D(Color.AQUA))),
            Sphere(viewportCenterLocation + Vector3D(1, 0, -30), 5, Material(Vector3D(Color.GREEN), Vector3D.ZERO))
        )


        (0 until Parameters.IMAGE_WIDTH).foreach(x => {
            ((Parameters.IMAGE_HEIGHT - 1) to 0 by -1).foreach(y => {

                val planeLocation =
                    nw + (Vector3D.RIGHT * x * planeWidth / Parameters.IMAGE_WIDTH) + (-Vector3D.UP * y * planeHeight / Parameters.IMAGE_HEIGHT) +
                            Vector3D(planeWidth / Parameters.IMAGE_WIDTH / 2, -planeHeight / Parameters.IMAGE_HEIGHT / 2, 0)

                val colorAccumulator =
                for (s <- 1 to Parameters.SAMPLE_COUNT) yield {
                    val rayIntoScene = Ray3D(eyeLocation, eyeLocation to planeLocation)
                    castRay(rayIntoScene, geometryList, 0)
                }

                val color = colorAccumulator.reduce((c1, c2) => c1 + c2) / colorAccumulator.length

                val finalColor = Vector3D(
                    ShadingUtil.clamp(color.x, 0, 1),
                    ShadingUtil.clamp(color.y, 0, 1),
                    ShadingUtil.clamp(color.z, 0, 1)
                )

                pixelWriter.setColor(x, y, finalColor.toColor)
            })
            println(s"COL COMPLETED: $x")
        })

        // image filling occurs here

        writeImageToFile(image, getFile)
    }

    def castRay(ray: Ray3D, geometry: List[Geometry], depth: Int): Vector3D = {

        if (depth > Parameters.BOUNCE_COUNT) return Vector3D(Color.BLACK)

        val collisions: List[RayHit] =
            (for (g <- geometry) yield g intersections ray).flatten.filter(_.time >= Parameters.TIME_THRESHOLD)

        if (collisions.isEmpty) return Vector3D(Color.BLACK)

        val collision = collisions.reduce((rh1, rh2) => if (rh1.time <= rh2.time) rh1 else rh2)

        val g = collision.geometry
        val p = collision.ray.pointAt(collision.time)
        val m = g.material

        val randomRay = Ray3D(p, g.normalAt(p).inHemisphereOf)

        val probability = 1 / (2 * math.Pi)
        val cosineTheta = randomRay.direction dot g.normalAt(p)
        val BRDF = m.reflectance / math.Pi

        val incomingColor = castRay(randomRay, geometry, depth + 1)

        //println(s"Data: emit = ${m.emittance}, BRDF = $BRDF, inCol = $incomingColor")

        val finalColor = m.emittance + (BRDF * incomingColor * cosineTheta / probability)
        if (finalColor != Vector3D.ZERO && depth == 0) {
            if (finalColor.x > 1 || finalColor.x < 0 || finalColor.y > 1 || finalColor.y < 0 || finalColor.z > 1 || finalColor.z < 0) {
//                println(s"Data: emit = ${m.emittance}, BRDF = $BRDF, inCol = $incomingColor")
//                println(s"FINAL COLOR: $finalColor")
            }
        }

        finalColor
    }

    def getImage: WritableImage = new WritableImage(Parameters.IMAGE_WIDTH, Parameters.IMAGE_HEIGHT)

    def getFile: File = {
        val dir = Parameters.FILEPATH
        if (!dir.exists && !dir.mkdirs()) {
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
