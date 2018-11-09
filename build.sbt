
name := "luminescence"

version := "0.5"

scalaVersion := "2.12.7"

mainClass in assembly := Some("me.paul.luminescence.Luminescence")
assemblyJarName in assembly := name.value + "-" + version.value + "_fat-executable.jar"
