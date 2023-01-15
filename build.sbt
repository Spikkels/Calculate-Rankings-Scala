ThisBuild / scalaVersion := "2.13.10"

libraryDependencies += "dev.zio" %% "zio" % "2.0.5"
libraryDependencies += "dev.zio" %% "zio-streams" % "2.0.5"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-test"          % "2.0.5" % Test,
  "dev.zio" %% "zio-test-sbt"      % "2.0.5" % Test,
  "dev.zio" %% "zio-test-magnolia" % "2.0.5" % Test
)
testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

lazy val root = (project in file("."))
  .settings(
    name := "git-branch-checker"
  )