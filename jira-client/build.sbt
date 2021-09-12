name := "jira-client"

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient"           % "4.5.3",
  "io.spray"                 %% "spray-json"           % "1.3.6",
  "io.lemonlabs"             %% "scala-uri"            % "1.5.1",
  "org.slf4j"                 % "slf4j-api"            % "1.7.31",
  "org.scalatest"            %% "scalatest"            % "3.0.5" % "test",
  "org.specs2"               %% "specs2-core"          % "4.12.12" % Test,
  "org.specs2"               %% "specs2-matcher"       % "4.12.12" % Test,
  "org.specs2"               %% "specs2-matcher-extra" % "4.12.12" % Test,
  "org.specs2"               %% "specs2-mock"          % "4.12.12" % Test
)
