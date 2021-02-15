lazy val root = project
  .in(file("."))
  .settings(
    name := "meow",
    version := dhall.config.version,

    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.21" % Test,
      "org.scalameta" %% "munit-scalacheck" % "0.7.21" % Test,
    ),
    scalaVersion := dhall.config.scalaVersion,
    crossScalaVersions := List(dhall.config.scalaVersion),
    Compile / scalacOptions ++= Seq(
      "-Ykind-projector",
      "-rewrite",
      "-indent",
      "-language:implicitConversions",
    ),
    Compile / doc / scalacOptions ++= Seq(
      "-siteroot", "docs",
      "-d", "docs/_site",
      "-project-version", dhall.config.version,
    ),
    testFrameworks += new TestFramework("munit.Framework"),
  )

inScope(Scope.GlobalScope)(
  List(
    organization := "us.oyanglul",
    licenses := Seq("Apache License 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    homepage := Some(url("https://github.com/jcouyang/meow")),
    developers := List(
      Developer("jcouyang", "Jichao Ouyang", "oyanglulu@gmail.com", url("https://github.com/jcouyang"))
    ),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/jcouyang/meow"),
        "scm:git@github.com:jcouyang/meow.git"
      )
    ),
    pgpPublicRing := file(".") / ".gnupg" / "pubring.asc",
    pgpSecretRing := file(".") / ".gnupg" / "secring.asc",
    releaseEarlyWith := SonatypePublisher,
  )
)
