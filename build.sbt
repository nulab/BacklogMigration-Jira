import sbt.Keys._

lazy val commonSettings = Seq(
  organization := "com.nulabinc",
  version := "0.1.0b1",
  scalaVersion := "2.11.6",
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
  resolvers ++= Seq("snapshots", "releases").map(Resolver.sonatypeRepo),
  libraryDependencies ++= Seq(
    "com.osinka.i18n"               %  "scala-i18n_2.11"        % "1.0.0",
    "ch.qos.logback"                %  "logback-classic"        % "1.1.3",
    "io.spray"                      %  "spray-json_2.11"        % "1.3.2",
    "com.github.scala-incubator.io" %  "scala-io-core_2.11"     % "0.4.3",
    "com.github.scala-incubator.io" %  "scala-io-file_2.11"     % "0.4.3",
    "com.typesafe"                  %  "config"                 % "1.3.0",
    "joda-time"                     %  "joda-time"              % "2.3",
    "org.joda"                      %  "joda-convert"           % "1.6",
    "com.google.inject"             %  "guice"                  % "4.1.0",
    "com.netaporter"                %% "scala-uri"              % "0.4.16",
    "org.fusesource.jansi"          %  "jansi"                  % "1.11",
    "com.mixpanel"                  %  "mixpanel-java"          % "1.4.4",
    "org.scalatest"                 %% "scalatest"              % "3.0.1"   % "test",
    "org.specs2"                    %% "specs2-core"            % "3.8.9"   % Test,
    "org.specs2"                    %% "specs2-matcher"         % "3.8.9"   % Test,
    "org.specs2"                    %% "specs2-matcher-extra"   % "3.8.9"   % Test,
    "org.specs2"                    %% "specs2-mock"            % "3.8.9"   % Test
  ),
  javacOptions ++= Seq("-encoding", "UTF-8")
)

lazy val common = (project in file("common"))
  .settings(commonSettings: _*)
  .settings(
    name := "backlog-migration-common",
    unmanagedBase := baseDirectory.value / "libs",
    scapegoatVersion := "1.1.0",
    scapegoatDisabledInspections := Seq("NullParameter", "CatchThrowable", "NoOpOverride")
  )

lazy val importer = (project in file("importer"))
  .settings(commonSettings: _*)
  .settings(
    name := "backlog-importer",
    scapegoatVersion := "1.1.0",
    scapegoatDisabledInspections := Seq("NullParameter", "CatchThrowable", "NoOpOverride")
  )
  .dependsOn(common % "test->test;compile->compile")
  .aggregate(common)

lazy val exporter = (project in file("exporter"))
  .settings(commonSettings: _*)
  .settings(
    name := "backlog-jira-exporter",
    scapegoatVersion := "1.1.0",
    scapegoatDisabledInspections := Seq(
      "NullParameter",
      "CatchThrowable",
      "NoOpOverride"
    )
  )
  .dependsOn(jira, client)

lazy val jira = (project in file("jira"))
  .settings(commonSettings: _*)
  .settings(
    name := "jira",
    scapegoatVersion := "1.1.0",
    scapegoatDisabledInspections := Seq(
      "NullParameter",
      "CatchThrowable",
      "NoOpOverride"
    )
  )
  .dependsOn(common % "test->test;compile->compile", client)
  .aggregate(common, client)

lazy val mappingBase = (project in file("mapping-base"))
  .settings(commonSettings: _*)
  .settings(
    name := "backlog-jira-mapping-base",
    scapegoatVersion := "1.1.0",
    scapegoatDisabledInspections := Seq(
      "NullParameter",
      "CatchThrowable",
      "NoOpOverride"
    )
  )
  .dependsOn(common % "test->test;compile->compile", jira)

lazy val mappingConverter = (project in file("mapping-converter"))
  .settings(commonSettings: _*)
  .settings(
    name := "backlog-jira-mapping-converter",
    scapegoatVersion := "1.1.0",
    scapegoatDisabledInspections := Seq(
      "NullParameter",
      "CatchThrowable",
      "NoOpOverride"
    )
  )
  .dependsOn(mappingBase, mappingFile)

//lazy val mappingCollector = (project in file("mapping-collector"))
//  .settings(commonSettings: _*)
//  .settings(
//    name := "backlog-jira-mapping-collector",
//    scapegoatVersion := "1.1.0",
//    scapegoatDisabledInspections := Seq(
//      "NullParameter",
//      "CatchThrowable",
//      "NoOpOverride"
//    )
//  )
//  .dependsOn(mappingBase)

lazy val mappingFile = (project in file("mapping-file"))
  .settings(commonSettings: _*)
  .settings(
    name := "backlog-jira-mapping-file",
    scapegoatVersion := "1.1.0",
    scapegoatDisabledInspections := Seq(
      "NullParameter",
      "CatchThrowable",
      "NoOpOverride"
    )
  )
  .dependsOn(jira, mappingBase, client)

lazy val writer = (project in file("project-writer"))
  .settings(commonSettings: _*)
  .settings(
    name := "backlog-jira-project-writer",
    scapegoatVersion := "1.1.0",
    scapegoatDisabledInspections := Seq(
      "NullParameter",
      "CatchThrowable",
      "NoOpOverride"
    )
  )
  .dependsOn(jira, client)

lazy val reader = (project in file("project-reader"))
  .settings(commonSettings: _*)
  .settings(
    name := "backlog-jira-project-reader",
    scapegoatVersion := "1.1.0",
    scapegoatDisabledInspections := Seq(
      "NullParameter",
      "CatchThrowable",
      "NoOpOverride"
    )
  )
  .dependsOn(jira)

lazy val client = (project in file("jira-client"))
  .settings(commonSettings: _*)
  .settings(
    name := "backlog-jira-client",
    libraryDependencies ++= Seq(
      "org.apache.httpcomponents" %  "httpclient" % "4.5.3"
    ),
    scapegoatVersion := "1.1.0",
    scapegoatDisabledInspections := Seq(
      "NullParameter",
      "CatchThrowable",
      "NoOpOverride"
    )
  )

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    name := "backlog-migration-jira",
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest"     % "3.0.1"   % "test",
      "org.rogach"    %  "scallop_2.11"  % "2.0.5"
    ),
    assemblyJarName in assembly := {
      s"${name.value}-${version.value}.jar"
    },
    testOptions in Test ++= Seq(
      Tests.Argument(TestFrameworks.ScalaTest, "-u", "target/test-reports"),
      Tests.Argument(TestFrameworks.ScalaTest, "-f", "target/test-reports/output.txt")
    ),
    test in assembly := {},
    scapegoatVersion := "1.1.0",
    scapegoatDisabledInspections := Seq("NullParameter", "CatchThrowable", "NoOpOverride")
  )
  .dependsOn(common % "test->test;compile->compile", importer, exporter, writer, reader, client, jira, mappingFile, mappingConverter)
  .aggregate(common, importer, exporter, writer, reader, client, jira, mappingFile, mappingConverter)