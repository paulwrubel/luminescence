package me.paul.luminescence

import java.awt.Desktop
import java.io.{File, IOException}

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import javax.imageio.ImageIO
import me.paul.luminescence.LoopUtil._
import me.paul.luminescence.geometry._
import me.paul.luminescence.shading.material.{LambertianMaterial, MetalMaterial}

object Luminescence {

    def main(args: Array[String]): Unit = {
        val image = getImage
        val file = getFile
        val scene = getScene

        timeOf(0) {
            render(image, scene)
        }

        // image filling occurs here

        writeImageToFile(image, file)
        openFile(file)
    }

    def render(image: WritableImage, scene: Scene): Unit = {


        val threads = List(
            new Thread(new Tracer(1, 0, Parameters.IMAGE_WIDTH / 2 - 1, 0, Parameters.IMAGE_HEIGHT / 2 - 1, image, scene)),
            new Thread(new Tracer(2, Parameters.IMAGE_WIDTH / 2, Parameters.IMAGE_WIDTH - 1, 0, Parameters.IMAGE_HEIGHT / 2 - 1, image, scene)),
            new Thread(new Tracer(3, 0, Parameters.IMAGE_WIDTH / 2 - 1, Parameters.IMAGE_HEIGHT / 2, Parameters.IMAGE_HEIGHT - 1, image, scene)),
            new Thread(new Tracer(4, Parameters.IMAGE_WIDTH / 2, Parameters.IMAGE_WIDTH - 1, Parameters.IMAGE_HEIGHT / 2, Parameters.IMAGE_HEIGHT - 1, image, scene))
        )

        threads.foreach(_.start)

        println("RUNNING THREADS")

        threads.foreach(_.join)

        println("FINISHED")

    }

    def getScene: Scene = {

        val width = 10.0
        val height = 10.0
        val depth = 10.0
        val lightWidth = 2.0
        val lightDepth = 2.0

        val vfov = 40.0

        val eyeLocation = Point3D(width / 2.0, height / 2.0, (height / 2.0) / math.tan(math.toRadians(vfov / 2)))
        val lookAt = Point3D(width / 2.0, height / 2.0, 0.0)

        val camera = Camera(eyeLocation, lookAt, Vector3D.UP, vfov, Parameters.IMAGE_WIDTH.asInstanceOf[Double] / Parameters.IMAGE_HEIGHT)

        val farUpperLeft   = Point3D(0 , height, -depth)
        val farUpperRight  = Point3D(width, height, -depth)
        val farLowerLeft   = Point3D(0 ,  0, -depth)
        val farLowerRight  = Point3D(width,  0, -depth)
        val nearUpperLeft  = Point3D(0 , height,   0)
        val nearUpperRight = Point3D(width, height,   0)
        val nearLowerLeft  = Point3D(0 ,  0,   0)
        val nearLowerRight = Point3D(width,  0,   0)

        val ceilingCenter = Point3D(width / 2.0, height, -depth / 2.0)

        val lightFarLeft = ceilingCenter + Vector3D(-lightWidth / 2.0,  -0.001, -lightDepth / 2.0)
        val lightFarRight = ceilingCenter + Vector3D(lightWidth / 2.0,  -0.001, -lightDepth / 2.0)
        val lightNearLeft = ceilingCenter + Vector3D(-lightWidth / 2.0, -0.001, lightDepth / 2.0)
        val lightNearRight = ceilingCenter + Vector3D(lightWidth / 2.0, -0.001, lightDepth / 2.0)

        val geometry = List(
            // Left Wall
            Triangle(nearLowerLeft, farLowerLeft, farUpperLeft, LambertianMaterial(Vector3D(0.9, 0.1, 0.1))),
            Triangle(nearLowerLeft, farUpperLeft, nearUpperLeft, LambertianMaterial(Vector3D(0.9, 0.1, 0.1))),
            // Back Wall
            Triangle(farLowerLeft, farLowerRight, farUpperRight, LambertianMaterial(Vector3D(Color.WHITESMOKE))),
            Triangle(farLowerLeft, farUpperRight, farUpperLeft, LambertianMaterial(Vector3D(Color.WHITESMOKE))),
            // Front Wall
            Triangle(nearLowerRight, nearLowerLeft, nearUpperLeft, LambertianMaterial(Vector3D(Color.WHITESMOKE))),
            Triangle(nearLowerRight, nearUpperLeft, nearUpperRight, LambertianMaterial(Vector3D(Color.WHITESMOKE))),
            // Right Wall
            Triangle(farLowerRight, nearLowerRight, nearUpperRight, LambertianMaterial(Vector3D(0.1, 0.9, 0.1))),
            Triangle(farLowerRight, nearUpperRight, farUpperRight, LambertianMaterial(Vector3D(0.1, 0.9, 0.1))),
            // Floor
            Triangle(nearLowerLeft, nearLowerRight, farLowerRight, LambertianMaterial(Vector3D(Color.WHITESMOKE))),
            Triangle(nearLowerLeft, farLowerRight, farLowerLeft, LambertianMaterial(Vector3D(Color.WHITESMOKE))),
            // Ceiling
            Triangle(farUpperLeft, farUpperRight, nearUpperRight, LambertianMaterial(Vector3D(Color.WHITESMOKE))),
            Triangle(farUpperLeft, nearUpperRight, nearUpperLeft, LambertianMaterial(Vector3D(Color.WHITESMOKE))),
            // Ceiling Light
            Triangle(lightFarLeft, lightFarRight, lightNearRight, LambertianMaterial.light(Vector3D(2))),
            Triangle(lightFarLeft, lightNearRight, lightNearLeft, LambertianMaterial.light(Vector3D(2))),

            Sphere(farLowerLeft + Vector3D(3, 2, 7), 2, LambertianMaterial(Vector3D(0.9))),
            Sphere(farLowerRight + Vector3D(-4, 4, 3), 3, MetalMaterial(Vector3D(1)))
//            Sphere(Point3D(15, 1, -19), 1, LambertianMaterial(Vector3D(0.1, 0.9, 0.9), Vector3D.ZERO))
                )

        Scene(camera, geometry)
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

    def openFile(file: File): Unit = Desktop.getDesktop.open(file)

    def timeOf[A](flag: Int)(block: => A): A = {

        val time0 = System.nanoTime()
        val result = block
        val time1 = System.nanoTime()

        flag match {
            case 0 =>
                println(f"Time: ${(time1 - time0) / 1000000000.0}%.2fs")
            case 1 =>
                println(f"Time: ${(time1 - time0) / 1000000.0}%.2fms")
            case 2 =>
                println(f"Time: ${(time1 - time0) / 1000.0}%.2fns")
        }
        result

    }
}
