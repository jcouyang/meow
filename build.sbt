lazy val root = project
  .in(file("."))
  .settings(
    name := "meow",
    version := dhall.config.version,

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.21" % Test,
      "org.scalameta" %% "munit-scalacheck" % "0.7.21" % Test,
    ),
    // To make the default compiler and REPL use Dotty
    scalaVersion := dhall.config.scalaVersion,
    scalacOptions ++= Seq(
      "-Ykind-projector",
      "-rewrite",
      "-indent",
      "-language:implicitConversions",
    ),
    Compile / doc / scalacOptions ++= Seq(
      "-siteroot", "docs",
      "-d", "docs/_site",
      "-project-version", dhall.config.version,
      "-project-url", "https://github.com/jcouyang/meow",
    ),
    testFrameworks += new TestFramework("munit.Framework"),
  )
