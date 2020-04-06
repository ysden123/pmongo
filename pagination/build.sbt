import sbt.Keys.scalacOptions

lazy val loggingVersion = "2.13.1"
lazy val vertxVersion = "3.6.0"
lazy val rxJavaVersion = "2.1.13"

lazy val root = (project in file(".")).
  settings(
    name := "pagination",
    version := "1.0.0",
    scalaVersion := "2.13.1",
    javacOptions ++= Seq("-source", "11"),
    libraryDependencies ++= {
      Seq(
        "io.vertx" % "vertx-core" % vertxVersion,
        "io.vertx" % "vertx-config" % vertxVersion,
        "io.vertx" % "vertx-rx-java2" % vertxVersion,
        "io.vertx" % "vertx-mongo-client" % vertxVersion,
        "io.reactivex.rxjava2" % "rxjava" % rxJavaVersion,
        "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
        "org.apache.logging.log4j" % "log4j-api" % loggingVersion,
        "org.apache.logging.log4j" % "log4j-core" % loggingVersion,
        "org.apache.logging.log4j" % "log4j-slf4j-impl" % loggingVersion
      )
    },
    scalacOptions in(Compile, doc) ++= Seq("-author"),
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-language:implicitConversions",
      "-language:postfixOps"
    )
  )