name := "HAVacationRental"

version := "1.0"

scalaVersion := "2.11.11" // Finagle does NOT support scala 2.12

resolvers ++= Seq(
  "Twitter repository" at "http://maven.twttr.com"
)

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-http" % "6.45.0",
  "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.8.1",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.8",

  "org.scalatest" %% "scalatest" % "3.0.3" % "test"
)