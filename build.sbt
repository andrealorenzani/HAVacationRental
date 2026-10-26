name := "HAVacationRental"

version := "2.0"

// Finagle is published for Scala 2.13 only (no Scala 3 artifacts)
scalaVersion := "2.13.18"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")

// Run in a forked JVM so that the server can be stopped with Ctrl-C
run / fork := true
Test / fork := true

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-http" % "24.2.0",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.22.4",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.22.4",
  "org.scalatest" %% "scalatest" % "3.2.19" % Test
)
