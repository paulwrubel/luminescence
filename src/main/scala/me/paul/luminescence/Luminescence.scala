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

        val scene = getScene

        render(image, scene)

        // image filling occurs here

        writeImageToFile(image, getFile)
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

    def castRay(ray: Ray3D, geometry: List[Geometry], depth: Int): Vector3D = {

        val a = Vector3D(1, 2, 3)

        val b = -a

        val c = a + b

        val d = a cross b

        val x =
            if ((a cross b) == Vector3D.ZERO) 5 else 7

        if (depth > Parameters.BOUNCE_COUNT) return Vector3D(Color.BLACK)

        val collisions: List[RayHit] =
            (for (g <- geometry) yield g intersections ray).flatten.filter(_.time >= Parameters.TIME_THRESHOLD)

        if (collisions.isEmpty) return Vector3D(Color.BLACK)

        val collision = collisions.reduce((rh1, rh2) => if (rh1.time <= rh2.time) rh1 else rh2)

        val g = collision.geometry
        val p = collision.ray.pointAt(collision.time)
        val m = g.material

        if (m.reflectance == Vector3D.ZERO) return m.emittance

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

    def getScene: Scene = {
        val farUpperLeft   = Point3D(0 , 10, -20)
        val farUpperRight  = Point3D(20, 10, -20)
        val farLowerLeft   = Point3D(0 ,  0, -20)
        val farLowerRight  = Point3D(20,  0, -20)
        val nearUpperLeft  = Point3D(0 , 10,   0)
        val nearUpperRight = Point3D(20, 10,   0)
        val nearLowerLeft  = Point3D(0 ,  0,   0)
        val nearLowerRight = Point3D(20,  0,   0)

        Scene(
            Point3D(10, 5, 10),
            ViewPort(Point3D(0, 10, 0), Point3D(20, 0, 0)),
            List(
                Sphere(Point3D(1, 9, -19), 1, Material(Vector3D.ZERO, Vector3D(25, 25, 25))),
                Triangle(nearLowerLeft, farLowerLeft, farUpperLeft, Material(Vector3D(Color.WHITESMOKE), Vector3D.ZERO)),
                Triangle(nearLowerLeft, farUpperLeft, nearUpperLeft, Material(Vector3D(Color.WHITESMOKE), Vector3D.ZERO)),
                Triangle(farLowerLeft, farLowerRight, farUpperRight, Material(Vector3D(Color.WHITESMOKE), Vector3D.ZERO)),
                Triangle(farLowerLeft, farUpperRight, farUpperLeft, Material(Vector3D(Color.WHITESMOKE), Vector3D.ZERO)),
                Triangle(farLowerRight, nearLowerRight, nearUpperRight, Material(Vector3D(Color.WHITESMOKE), Vector3D.ZERO)),
                Triangle(farLowerRight, nearUpperRight, farUpperRight, Material(Vector3D(Color.WHITESMOKE), Vector3D.ZERO)),
                Triangle(nearLowerLeft, nearLowerRight, farLowerRight, Material(Vector3D(Color.WHITESMOKE), Vector3D.ZERO)),
                Triangle(nearLowerLeft, farLowerRight, farLowerLeft, Material(Vector3D(Color.WHITESMOKE), Vector3D.ZERO)),
                Triangle(farUpperLeft, farUpperRight, nearUpperRight, Material(Vector3D(Color.WHITESMOKE), Vector3D.ZERO)),
                Triangle(farUpperLeft, nearUpperRight, nearUpperLeft, Material(Vector3D(Color.WHITESMOKE), Vector3D.ZERO)),
                Sphere(Point3D(19, 1, -15), 1, Material.light(Vector3D(Color.RED) * 5)),
                Sphere(Point3D(18, 2, -18), 2, Material(Vector3D(Color.color(0.1, 0.1, 0.9)), Vector3D.ZERO)),
                Sphere(Point3D(15, 1, -19), 1, Material.light(Vector3D(Color.AQUA) * 2))
            )
        )
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
