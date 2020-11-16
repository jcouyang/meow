val dottyVersion = "3.0.0-M1"
val scala213Version = "2.13.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotty-cross",
    version := "0.2.0",

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.17" % Test,
      "org.scalameta" %% "munit-scalacheck" % "0.7.17" % Test,
      "com.chuusai" % "shapeless_2.13" % "2.3.3" % Test,
    ),
    // To make the default compiler and REPL use Dotty
    scalaVersion := dottyVersion,
    scalacOptions ++= Seq("-Ykind-projector", "-language:implicitConversions"),
    testFrameworks += new TestFramework("munit.Framework"),
    // To cross compile with Dotty and Scala 2
    // crossScalaVersions := Seq(dottyVersion, scala213Version)
  )
