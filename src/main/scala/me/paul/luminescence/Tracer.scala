package me.paul.luminescence

import javafx.scene.image.WritableImage
import me.paul.luminescence.Luminescence.castRay
import me.paul.luminescence.geometry.{Ray3D, Vector3D}
import me.paul.luminescence.shading.ShadingUtil

class Tracer(val id: Int, val xMin: Int, val xMax: Int, val yMin: Int, val yMax: Int, val image: WritableImage, val scene: Scene) extends Runnable{

    override def run(): Unit = {

        val pixelWriter = image.getPixelWriter

        val nw = scene.viewport.northwest
        val se = scene.viewport.southeast

        val planeWidth: Double = (nw to se).x.abs
        val planeHeight: Double = (nw to se).y.abs

        val a= 2.+(3)

        (1 to 10).map(x => x + 2)

        (xMin to xMax).foreach(x => {
            (yMax to yMin by -1).foreach(y => {

                val planeLocation =
                    nw +
                            (Vector3D.RIGHT * x * planeWidth / Parameters.IMAGE_WIDTH) +
                            (-Vector3D.UP * y * planeHeight / Parameters.IMAGE_HEIGHT) +
                            Vector3D(RandomUtil.randomUpTo(planeWidth / Parameters.IMAGE_WIDTH), -RandomUtil.randomUpTo(planeHeight / Parameters.IMAGE_HEIGHT), 0)

                val colorAccumulator =
                    for (s <- 1 to Parameters.SAMPLE_COUNT) yield {
                        val rayIntoScene = Ray3D(scene.eyeLocation, scene.eyeLocation to planeLocation)
                        castRay(rayIntoScene, scene.geometry, 0)
                    }

                val color = colorAccumulator.reduce((c1, c2) => c1 + c2) / colorAccumulator.length

                val finalColor = Vector3D(
                    ShadingUtil.clamp(color.x, 0, 1),
                    ShadingUtil.clamp(color.y, 0, 1),
                    ShadingUtil.clamp(color.z, 0, 1)
                )

                pixelWriter.setColor(x, y, finalColor.toColor)
            })
            println(f"Chunk #$id: ${100 * (x - xMin).asInstanceOf[Double] / (xMax - xMin)}%3.4f%%")
        })
    }

}
