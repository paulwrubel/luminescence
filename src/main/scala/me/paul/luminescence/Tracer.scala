package me.paul.luminescence

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import me.paul.luminescence.geometry.{Geometry, Ray3D, Vector3D}
import me.paul.luminescence.shading.{RayHit, ShadingUtil}

class Tracer(val id: Int, val xMin: Int, val xMax: Int, val yMin: Int, val yMax: Int, val image: WritableImage, val scene: Scene) extends Runnable{

    override def run(): Unit = {

        val pixelWriter = image.getPixelWriter

        (yMin to yMax).foreach(y => {
            (xMin to xMax).foreach(x => {

                var colorAccumulator: Vector3D = Vector3D.ZERO

                for (s <- 1 to Parameters.SAMPLE_COUNT) yield {
                    val xScale = (x + RandomUtil.randomUpTo(1)) / Parameters.IMAGE_WIDTH
                    val yScale = (y + RandomUtil.randomUpTo(1)) / Parameters.IMAGE_HEIGHT

                    val rayIntoScene = scene.camera.getRay(xScale, yScale)
                    colorAccumulator += castRay(rayIntoScene, scene.geometry, 0)
                }

                val color = colorAccumulator / Parameters.SAMPLE_COUNT

                val finalColor = Vector3D(
                    ShadingUtil.clamp(color.x, 0, 1),
                    ShadingUtil.clamp(color.y, 0, 1),
                    ShadingUtil.clamp(color.z, 0, 1)
                )

                pixelWriter.setColor(x, Parameters.IMAGE_HEIGHT - y - 1, finalColor.toColor)
            })
            println(f"Chunk #$id: ${100 * (y - yMin).asInstanceOf[Double] / (yMax - yMin)}%3.4f%%")
        })
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

}
