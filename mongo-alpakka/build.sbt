import sbt.Keys.libraryDependencies

lazy val akkaVersion = "2.6.9"
lazy val alpakkaMongodbVersion = "2.0.2"
lazy val mongoScalaDriver = "4.1.0"
lazy val scalaLoggingVersion = "3.9.2"
lazy val loggingVersion = "2.13.3"
lazy val projectVersion = "1.0.0"
lazy val projectName = "mongo-alpakka"
lazy val typeSafeConfVersion = "1.4.0"

lazy val commonSettings = Seq(
  organization := "com.stulsoft",
  version := projectVersion,
  javacOptions ++= Seq("-source", "11"),
  scalaVersion := "2.13.3",
  scalacOptions ++= Seq(
    "-feature",
    "-deprecation",
    "-language:implicitConversions",
    "-language:postfixOps"),
  libraryDependencies ++= Seq(
    "com.lightbend.akka" %% "akka-stream-alpakka-mongodb" % alpakkaMongodbVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "org.mongodb.scala" %% "mongo-scala-driver" % mongoScalaDriver,
    "com.typesafe" % "config" % typeSafeConfVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "org.apache.logging.log4j" % "log4j-api" % loggingVersion,
    "org.apache.logging.log4j" % "log4j-core" % loggingVersion,
    "org.apache.logging.log4j" % "log4j-slf4j-impl" % loggingVersion
  )
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := projectName
  )
