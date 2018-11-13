package me.paul.luminescence

import java.awt.Desktop
import java.io.{File, IOException}

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import javax.imageio.ImageIO
import me.paul.luminescence.LoopUtil._
import me.paul.luminescence.geometry._
import me.paul.luminescence.geometry.primitive.{Box, _}
import me.paul.luminescence.shading.material.{DielectricMaterial, LambertianMaterial, MetalMaterial}

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
        val lightWidth = 5.0
        val lightDepth = 5.0

        val vfov = 40.0

        val eyeLocation = Point3D(width / 2.0, height / 2.0, (height / 2.0) / math.tan(math.toRadians(vfov / 2)))
        val lookAt = Point3D(width / 2.0, height / 2.0, 0.0)

        val camera = Camera(eyeLocation, lookAt, Vector3D.UP, vfov, Parameters.IMAGE_WIDTH.asInstanceOf[Double] / Parameters.IMAGE_HEIGHT)

        val farUpperLeft   = Point3D(0,     height, -depth)
        val farUpperRight  = Point3D(width, height, -depth)
        val farLowerLeft   = Point3D(0,     0,      -depth)
        val farLowerRight  = Point3D(width, 0,      -depth)
        val nearUpperLeft  = Point3D(0,     height, 0)
        val nearUpperRight = Point3D(width, height, 0)
        val nearLowerLeft  = Point3D(0,     0,      0)
        val nearLowerRight = Point3D(width, 0,      0)

        val whiteDiffuseMaterial = LambertianMaterial(Vector3D(0.9, 0.9, 0.9))
        val redDiffuseMaterial   = LambertianMaterial(Vector3D(0.9, 0.1, 0.1))
        val greenDiffuseMaterial = LambertianMaterial(Vector3D(0.1, 0.9, 0.1))
        val blueDiffuseMaterial  = LambertianMaterial(Vector3D(0.1, 0.1, 0.9))

        val whiteLightMaterial   = LambertianMaterial.light(Vector3D(1, 1, 1) * 1)
        val redLightMaterial     = LambertianMaterial.light(Vector3D(1, 0, 0) * 2)
        val greenLightMaterial   = LambertianMaterial.light(Vector3D(0, 1, 0) * 2)
        val blueLightMaterial    = LambertianMaterial.light(Vector3D(0, 0, 1) * 2)
        val cyanLightMaterial    = LambertianMaterial.light(Vector3D(0, 1, 1) * 2)
        val yellowLightMaterial  = LambertianMaterial.light(Vector3D(1, 1, 0) * 2)
        val magentaLightMaterial = LambertianMaterial.light(Vector3D(1, 0, 1) * 2)

        val waterMaterial   = DielectricMaterial(1.33)
        val glassMaterial   = DielectricMaterial(1.5)
        val diamondMaterial = DielectricMaterial(2.42)

        val metalMaterial        = MetalMaterial(0, Vector3D(1))
        val frostedMetalMaterial = MetalMaterial(0.2, Vector3D(1))

        val walls = List(
            // Left Wall
            Rectangle(nearLowerLeft, farUpperLeft, redDiffuseMaterial),
            // Right Wall
            Rectangle(farLowerRight, nearUpperRight, greenDiffuseMaterial, rn = true),
            // Back Wall
            Rectangle(farLowerLeft, farUpperRight, whiteDiffuseMaterial),
            // Front Wall
            Rectangle(nearLowerRight, nearUpperLeft, whiteDiffuseMaterial, ic = true, rn = true),
            // Floor
            Rectangle(nearLowerLeft, farLowerRight, whiteDiffuseMaterial),
            // Ceiling
            Rectangle(farUpperLeft, nearUpperRight, whiteDiffuseMaterial, rn = true)
        )

        val lights = List(
            // Ceiling Light - White
//            Rectangle(farUpperLeft + Vector3D(3, -0.0001, 3), farUpperLeft + Vector3D(7, -0.0001, 7), whiteLightMaterial, ic = true, rn = true),
            // Ceiling Light - Red
            Rectangle(farUpperLeft + Vector3D(0, -0.0001, 0),   farUpperLeft + Vector3D(3, -0.0001, 5),     redLightMaterial,     ic = true, rn = true),
            // Ceiling Light - Green
            Rectangle(farUpperLeft + Vector3D(3.5, -0.0001, 0), farUpperLeft + Vector3D(6.5, -0.0001, 5),   greenLightMaterial,   ic = true, rn = true),
            // Ceiling Light - Blue
            Rectangle(farUpperLeft + Vector3D(7, -0.0001, 0),   farUpperLeft + Vector3D(10, -0.0001, 5),    blueLightMaterial,    ic = true, rn = true),
            // Ceiling Light - Cyan
            Rectangle(farUpperLeft + Vector3D(0, -0.0001, 5),   farUpperLeft + Vector3D(3, -0.0001, 10),    cyanLightMaterial,    ic = true, rn = true),
            // Ceiling Light - Magenta
            Rectangle(farUpperLeft + Vector3D(3.5, -0.0001, 5), farUpperLeft + Vector3D(6.5, -0.0001, 10),  magentaLightMaterial, ic = true, rn = true),
            // Ceiling Light - Yellow
            Rectangle(farUpperLeft + Vector3D(7, -0.0001, 5),   farUpperLeft + Vector3D(10, -0.0001, 10),   yellowLightMaterial,  ic = true, rn = true)
        )

        val objects = List(
            // diffuse sphere
            Sphere(nearLowerLeft + Vector3D(2.5, 2.5, -2.5), 2, whiteDiffuseMaterial),
            // glass sphere
            Sphere(nearLowerLeft + Vector3D(5, 5, -5), 2, glassMaterial),
            // metal sphere
            Sphere(nearLowerLeft + Vector3D(7.5, 7.5, -7.5), 2, metalMaterial),
            // metal box
            Box(nearLowerRight + Vector3D(-3, 0, -1), nearLowerRight + Vector3D(-1, 2, -3), diamondMaterial),
            // red box
            Box(nearLowerRight + Vector3D(-3, 2, -1), nearLowerRight + Vector3D(-1, 3, -3), redDiffuseMaterial),
            // green box
            Box(nearLowerRight + Vector3D(-3, 3, -1), nearLowerRight + Vector3D(-1, 4, -3), greenDiffuseMaterial),
            // blue box
            Box(nearLowerRight + Vector3D(-3, 4, -1), nearLowerRight + Vector3D(-1, 5, -3), blueDiffuseMaterial)
        )

        val geometry: List[Geometry] = walls ::: lights ::: objects

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
