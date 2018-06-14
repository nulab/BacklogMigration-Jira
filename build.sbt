import sbt.Keys._

scapegoatVersion in ThisBuild := "1.3.3"

lazy val projectVersion = "0.3.0b3"

lazy val commonSettings = Seq(
  organization := "com.nulabinc",
  version := projectVersion,
  scalaVersion := "2.12.6",
  scalacOptions ++= Seq(
    "-language:reflectiveCalls",
    "-language:postfixOps",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-Xlint",
    "-Yrangepos",
    "-Ywarn-dead-code",
    "-Ywarn-unused",
    "-Ywarn-unused-import"
  ),
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest"            % "3.0.5"   % "test",
    "org.specs2"    %% "specs2-core"          % "3.8.9"   % Test,
    "org.specs2"    %% "specs2-matcher"       % "3.8.9"   % Test,
    "org.specs2"    %% "specs2-matcher-extra" % "3.8.9"   % Test,
    "org.specs2"    %% "specs2-mock"          % "3.8.9"   % Test
  ),
  scapegoatVersion := "1.3.4",
  scapegoatDisabledInspections := Seq("NullParameter", "CatchThrowable", "NoOpOverride"),
  javacOptions ++= Seq("-encoding", "UTF-8")
)

lazy val common = (project in file("common"))
  .settings(commonSettings)

lazy val importer = (project in file("importer"))
  .settings(commonSettings)
  .dependsOn(common)

lazy val client = (project in file("jira-client"))
  .settings(commonSettings)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    name := "backlog-migration-jira",
    libraryDependencies ++= Seq(
      "org.rogach" %% "scallop" % "3.1.2"
    ),
    assemblyJarName in assembly := {
      s"${name.value}-${version.value}.jar"
    },
    testOptions in Test ++= Seq(
      Tests.Argument(TestFrameworks.ScalaTest, "-u", "target/test-reports"),
      Tests.Argument(TestFrameworks.ScalaTest, "-f", "target/test-reports/output.txt")
    ),
    test in assembly := {}
  )
  .dependsOn(common % "test->test;compile->compile", importer, client)
  .aggregate(common, importer, client)