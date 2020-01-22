
lazy val projectVersion = "0.3.0b9"

lazy val commonSettings = Seq(
  organization := "com.nulabinc",
  version := projectVersion,
  scalaVersion := "2.13.1",
  libraryDependencies ++= {
    val catsVersion = "2.1.0"
    val monixVersion = "3.1.0"
    val spec2Version = "4.8.3"
    Seq(
      "org.typelevel" %% "cats-core"        % catsVersion,
      "org.typelevel" %% "cats-free"        % catsVersion,
      "io.monix"      %% "monix"            % monixVersion,
      "io.monix"      %% "monix-execution"  % monixVersion,
      "io.monix"      %% "monix-eval"       % monixVersion,
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

lazy val common = (project in file("common/core"))
  .settings(commonSettings)

lazy val importer = (project in file("common/importer"))
  .settings(commonSettings)
  .dependsOn(common)

lazy val client = (project in file("jira-client"))
  .settings(commonSettings)

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
  .dependsOn(common % "test->test;compile->compile", importer, client)
  .aggregate(common, importer, client)