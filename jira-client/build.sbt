name := "jira-client"

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient"           % "4.5.3",
  "io.spray"                 %% "spray-json"           % "1.3.5",
  "io.lemonlabs"             %% "scala-uri"            % "1.5.1",
  "org.slf4j"                 % "slf4j-api"            % "1.7.25",
  "org.scalatest"            %% "scalatest"            % "3.0.9" % "test",
  "org.specs2"               %% "specs2-core"          % "3.8.9" % Test,
  "org.specs2"               %% "specs2-matcher"       % "3.8.9" % Test,
  "org.specs2"               %% "specs2-matcher-extra" % "3.8.9" % Test,
  "org.specs2"               %% "specs2-mock"          % "3.8.9" % Test
)
