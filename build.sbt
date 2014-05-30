import AssemblyKeys._

assemblySettings

jarName in assembly := "Main.jar"

name := "Main"

version := "0.1"

scalaVersion := "2.10.4"

scalacOptions += "-deprecation"

libraryDependencies += "org.scala-lang" % "scala-swing" % "2.10.4"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.1.5" % "test"

mainClass := Some("org.nedervold.grammareditor.Main")

seq(appbundle.settings: _*)

appbundle.javaVersion := "1.6+"

appbundle.screenMenu := false

appbundle.name := "Main"

appbundle.normalizedName := "Main"

appbundle.organization := "org.nedervold"

appbundle.version := "0.2.0"

appbundle.icon := None 