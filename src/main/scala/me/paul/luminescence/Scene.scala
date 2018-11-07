package me.paul.luminescence

import me.paul.luminescence.geometry.{Geometry, Point3D}

class Scene(val eyeLocation: Point3D, val viewport: ViewPort, val geometry: List[Geometry]) {

}

object Scene {

    def apply(eyeLocation: Point3D, viewport: ViewPort, geometry: List[Geometry]): Scene = new Scene(eyeLocation, viewport, geometry)

}


case class ViewPort(northwest: Point3D, southeast: Point3D)
