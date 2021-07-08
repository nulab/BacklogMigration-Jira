lazy val commonSettings = Seq(
  organization := "com.nulabinc",
  version := "0.6.2-SNAPSHOT",
  scalaVersion := "2.13.6",
  scalacOptions ++= Seq(
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xlint",
    "-Wunused"
  ),
  libraryDependencies ++= {
    val spec2Version = "4.8.3"
    Seq(
      // test
      "org.scalatest" %% "scalatest"            % "3.1.0"      % "test",
      "org.specs2"    %% "specs2-core"          % spec2Version % Test,
      "org.specs2"    %% "specs2-matcher"       % spec2Version % Test,
      "org.specs2"    %% "specs2-matcher-extra" % spec2Version % Test,
      "org.specs2"    %% "specs2-mock"          % spec2Version % Test
    )
  },
  javacOptions ++= Seq("-encoding", "UTF-8"),
  assembly / test := {},
  // scalafix
  addCompilerPlugin(scalafixSemanticdb),
  semanticdbEnabled := true,
  semanticdbVersion := scalafixSemanticdb.revision
)

lazy val common = (project in file("common")).settings(commonSettings)

lazy val client = (project in file("jira-client")).settings(commonSettings)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "backlog-migration-jira",
    libraryDependencies ++= Seq(
      "com.github.scopt" %% "scopt" % "3.7.1"
    ),
    assembly / assemblyJarName := {
      s"${name.value}-${version.value}.jar"
    },
    Test / testOptions ++= Seq(
      Tests.Argument(TestFrameworks.ScalaTest, "-u", "target/test-reports"),
      Tests.Argument(TestFrameworks.ScalaTest, "-f", "target/test-reports/output.txt")
    )
  )
  .dependsOn(common % "test->test;compile->compile", client)
  .aggregate(common, client)

addCommandAlias(
  "fixAll",
  "all compile:scalafix; test:scalafix; scalafmt; test:scalafmt; scalafmtSbt"
)
addCommandAlias(
  "checkAll",
  "compile:scalafix --check; test:scalafix --check; scalafmtCheck; test:scalafmtCheck; scalafmtSbtCheck"
)

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.5.0"

Global / onChangedBuildSource := ReloadOnSourceChanges
