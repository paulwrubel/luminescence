
name := "luminescence"

version := "0.1"

scalaVersion := "2.12.7"

assemblyJarName in assembly := name.value + "_" + scalaVersion.value + "-" + version.value + "_fat-executable.jar"