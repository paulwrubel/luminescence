package me.paul.luminescence

import me.paul.luminescence.geometry.{Geometry, Point3D}

class Scene(val camera: Camera, val geometry: List[Geometry]) {

}

object Scene {

    def apply(c: Camera, g: List[Geometry]): Scene = new Scene(c, g)

}