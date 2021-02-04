val dottyVersion = "3.0.0-M3"
val scala213Version = "2.13.4"

lazy val root = project
  .in(file("."))
  .settings(
    name := "meow",
    version := "0.3.0",

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.21" % Test,
      "org.scalameta" %% "munit-scalacheck" % "0.7.21" % Test,
    ),
    // To make the default compiler and REPL use Dotty
    scalaVersion := dottyVersion,
    scalacOptions ++= Seq("-Ykind-projector", "-language:implicitConversions"),
    testFrameworks += new TestFramework("munit.Framework"),
  )
