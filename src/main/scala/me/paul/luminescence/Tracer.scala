package me.paul.luminescence

import javafx.scene.image.WritableImage
import javafx.scene.paint.Color
import me.paul.luminescence.geometry.{Geometry, Ray3D, Vector3D}
import me.paul.luminescence.shading.{RayHit, ShadingUtil}

class Tracer(val id: Int, val xMin: Int, val xMax: Int, val yMin: Int, val yMax: Int, val image: WritableImage, val scene: Scene) extends Runnable {

    override def run(): Unit = {

        val pixelWriter = image.getPixelWriter

        (yMin to yMax).foreach(y => {
            (xMin to xMax).foreach(x => {

                var colorAccumulator: Vector3D = Vector3D.ZERO

                (1 to Parameters.SAMPLE_COUNT).foreach(s => {
                    val xScale = (x + RandomUtil.randomUpTo(1)) / Parameters.IMAGE_WIDTH
                    val yScale = (y + RandomUtil.randomUpTo(1)) / Parameters.IMAGE_HEIGHT

                    val rayIntoScene = scene.camera.getRay(xScale, yScale)
                    colorAccumulator += colorOf(rayIntoScene, scene.geometry, 0)
                })

                val color = colorAccumulator / Parameters.SAMPLE_COUNT

                val finalColor =
                    if (Parameters.GAMMA_CORRECTION > 1) {
                        Vector3D(
                            math.pow(ShadingUtil.clamp(color.x, 0, 1), 1.0 / Parameters.GAMMA_CORRECTION),
                            math.pow(ShadingUtil.clamp(color.y, 0, 1), 1.0 / Parameters.GAMMA_CORRECTION),
                            math.pow(ShadingUtil.clamp(color.z, 0, 1), 1.0 / Parameters.GAMMA_CORRECTION)
                        )
                    } else {
                        Vector3D(
                            ShadingUtil.clamp(color.x, 0, 1),
                            ShadingUtil.clamp(color.y, 0, 1),
                            ShadingUtil.clamp(color.z, 0, 1)
                        )
                    }

                pixelWriter.setColor(x, Parameters.IMAGE_HEIGHT - y - 1, finalColor.toColor)
            })
            println(f"Chunk #$id: ${100 * (y - yMin).asInstanceOf[Double] / (yMax - yMin)}%3.4f%%")
        })
    }

    def colorOf(ray: Ray3D, geometry: List[Geometry], depth: Int): Vector3D = {

        if (depth > Parameters.BOUNCE_COUNT) return Vector3D(Color.BLACK)

        val collisions: List[RayHit] = geometry.flatMap(g => g.intersections(ray, Parameters.TIME_MINIMUM, Parameters.TIME_MAXIMUM))
        if (collisions.isEmpty) return Vector3D(Color.BLACK)
        val collision = collisions.min

        val g = collision.geometry
        val p = collision.ray.pointAt(collision.time)
        val m = g.material

        if (m.reflectance == Vector3D.ZERO) return m.emittance

        val randomRay = m.scatter(collision).get

        val probability = 1 / (2 * math.Pi)
        val cosineTheta = randomRay.direction dot g.normalAt(p)
        val BRDF = m.reflectance / math.Pi

        val incomingColor = colorOf(randomRay, geometry, depth + 1)

        //println(s"Data: emit = ${m.emittance}, BRDF = $BRDF, inCol = $incomingColor")

        val finalColor = m.emittance + (BRDF * incomingColor * cosineTheta / probability)

        finalColor
    }

}
