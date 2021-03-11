import sbt.Keys.{javacOptions, scalacOptions}

ThisBuild / scalaVersion := "2.13.4"
ThisBuild / organization := "com.stulsoft"
ThisBuild / version := "1.0.2"

lazy val loggingVersion = "2.14.0"

lazy val app = (project in file("."))
  .settings(
    name := "wp-tools",
    libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.2.2",

    libraryDependencies += "com.typesafe" % "config" % "1.4.1",

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
