val dottyVersion = "0.24.0-RC1"
val scala213Version = "2.13.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "dotty-cross",
    version := "0.1.0",

    // libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test",
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.7" % Test,
      "org.scalameta" %% "munit-scalacheck" % "0.7.7" % Test,
      "com.chuusai" % "shapeless_2.13" % "2.3.3" % Test,
    ),
    // To make the default compiler and REPL use Dotty
    scalaVersion := dottyVersion,
    scalacOptions ++= Seq("-Ykind-projector", "-language:implicitConversions"),
    testFrameworks += new TestFramework("munit.Framework"),
    // To cross compile with Dotty and Scala 2
    crossScalaVersions := Seq(dottyVersion, scala213Version)
  )
