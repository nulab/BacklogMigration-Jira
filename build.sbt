lazy val projectVersion = "0.5.0b1-SNAPSHOT"

lazy val commonSettings = Seq(
  organization := "com.nulabinc",
  version := projectVersion,
  scalaVersion := "2.13.3",
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
  test in assembly := {}
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
    assemblyJarName in assembly := {
      s"${name.value}-${version.value}.jar"
    },
    testOptions in Test ++= Seq(
      Tests.Argument(TestFrameworks.ScalaTest, "-u", "target/test-reports"),
      Tests.Argument(TestFrameworks.ScalaTest, "-f", "target/test-reports/output.txt")
    )
  )
  .dependsOn(common % "test->test;compile->compile", client)
  .aggregate(common, client)
