import sbt.Keys._

scapegoatVersion in ThisBuild := "1.3.3"

lazy val projectVersion = "0.3.0b2"

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

lazy val exporter = (project in file("exporter"))
  .settings(commonSettings)
  .dependsOn(jira, client, writer)

lazy val jira = (project in file("jira"))
  .settings(commonSettings)
  .dependsOn(common, client)

lazy val mappingBase = (project in file("mapping-base"))
  .settings(commonSettings)
  .dependsOn(common, jira)

lazy val mappingConverter = (project in file("mapping-converter"))
  .settings(commonSettings)
  .dependsOn(mappingBase, mappingFile)

lazy val mappingCollector = (project in file("mapping-collector"))
  .settings(commonSettings)
  .dependsOn(jira, mappingBase)

lazy val mappingFile = (project in file("mapping-file"))
  .settings(commonSettings)
  .dependsOn(jira, mappingBase, client)

lazy val writer = (project in file("project-writer"))
  .settings(commonSettings)
  .dependsOn(jira, client)

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
  .dependsOn(common % "test->test;compile->compile", importer, exporter, writer, client, jira, mappingFile, mappingConverter, mappingCollector)
  .aggregate(common, importer, exporter, writer, client, jira, mappingFile, mappingConverter)