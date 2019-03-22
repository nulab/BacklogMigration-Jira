
lazy val projectVersion = "0.3.0b6"

lazy val commonSettings = Seq(
  organization := "com.nulabinc",
  version := projectVersion,
  scalaVersion := "2.12.8",
  libraryDependencies ++= {
    val catsVersion = "1.6.0"
    val monixVersion = "3.0.0-RC1"
    Seq(
      "org.typelevel" %% "cats-core"            % catsVersion,
      "org.typelevel" %% "cats-free"            % catsVersion,
      "io.monix"      %% "monix"                % monixVersion,
      "io.monix"      %% "monix-execution"      % monixVersion,
      "io.monix"      %% "monix-eval"           % monixVersion,
      "org.scalatest" %% "scalatest"            % "3.0.5"       % "test",
      "org.specs2"    %% "specs2-core"          % "3.8.9"       % Test,
      "org.specs2"    %% "specs2-matcher"       % "3.8.9"       % Test,
      "org.specs2"    %% "specs2-matcher-extra" % "3.8.9"       % Test,
      "org.specs2"    %% "specs2-mock"          % "3.8.9"       % Test
    )
  },
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
      "com.github.scopt" %% "scopt" % "3.7.1"
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