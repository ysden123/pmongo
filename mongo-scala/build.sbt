import java.util.Calendar

import sbt.Keys.{javacOptions, scalacOptions}

ThisBuild / scalaVersion := "2.13.4"
ThisBuild / organization := "com.stulsoft"
ThisBuild / version := "1.0.1"

lazy val loggingVersion = "2.13.3"

lazy val app = (project in file("."))
  .settings(
    name := "mongo-scala",
    libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.1.1",
	
    libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % loggingVersion,
    libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % loggingVersion,
    libraryDependencies += "org.apache.logging.log4j" % "log4j-slf4j-impl" % loggingVersion,
    javacOptions ++= Seq("-source", "11"),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-language:implicitConversions",
      "-language:postfixOps")
  )
