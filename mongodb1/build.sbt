import sbt.Keys.scalacOptions

lazy val loggingVersion = "2.13.1"

lazy val root = (project in file(".")).
  settings(
    name := "mongodb1",
    version := "1.0.0",
    scalaVersion := "2.13.3",
    javacOptions ++= Seq("-source", "11"),

    libraryDependencies ++= {
      Seq(
        "org.mongodb.scala" %% "mongo-scala-driver" % "4.1.0",
        "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
        "org.apache.logging.log4j" % "log4j-api" % loggingVersion,
        "org.apache.logging.log4j" % "log4j-core" % loggingVersion,
        "org.apache.logging.log4j" % "log4j-slf4j-impl" % loggingVersion,
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