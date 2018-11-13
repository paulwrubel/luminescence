package me.paul.luminescence.shading.material

import me.paul.luminescence.RandomUtil
import me.paul.luminescence.geometry.{Ray3D, Vector3D}
import me.paul.luminescence.shading.RayHit

class DielectricMaterial(refractiveIndex: Double, val reflectance: Vector3D, val emittance: Vector3D) extends Material {

    override def isSpecular = true

    override def scatter(hit: RayHit): Option[Ray3D] = {
        val hitPoint = hit.ray.pointAt(hit.time)
        val normal = hit.normalAtHit
        val reflectedVector = hit.ray.direction.reflectAround(normal)

        val (refractiveNormal: Vector3D, ratioOfRefractiveIndices: Double, cosine: Double) =
            if ((hit.ray.direction dot normal) > 0) {
                val n = -normal
                val rri = refractiveIndex
                val preCos = hit.ray.direction dot normal
                val cos = math.sqrt(1.0 - (refractiveIndex * refractiveIndex) * (1.0 - (preCos * preCos)))
                (n, rri, cos)
            } else {
                val n = normal
                val rri = 1.0 / refractiveIndex
                val cos = -(hit.ray.direction dot normal)
                (n, rri, cos)
            }

        val refractedVectorOption = hit.ray.direction.refractAround(refractiveNormal, ratioOfRefractiveIndices)
        val reflectionProbability =
            if (refractedVectorOption.isDefined) {
                schlick(cosine, refractiveIndex)
            } else {
                1.0
            }

        if (RandomUtil.randomUpTo(1.0) < reflectionProbability) {
            Some(Ray3D(hitPoint, reflectedVector))
        } else {
            Some(Ray3D(hitPoint, refractedVectorOption.get))
        }


    }

    private def schlick(cosine: Double, refractiveIndex: Double): Double = {
        val r0 = (1.0 - refractiveIndex) / (1.0 + refractiveIndex)
        val r1 = r0 * r0
        r1 + (1.0 - r1) * math.pow(1.0 - cosine, 5)
    }

}

object DielectricMaterial {

    def apply(ri: Double, r: Vector3D, e: Vector3D): DielectricMaterial = new DielectricMaterial(ri, r, e)

    def apply(ri: Double, r: Vector3D): DielectricMaterial = new DielectricMaterial(ri, r, Vector3D.ZERO)

    def apply(ri: Double): DielectricMaterial = new DielectricMaterial(ri, Vector3D(1), Vector3D.ZERO)

}
