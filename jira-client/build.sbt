
name := "jira-client"

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" %  "httpclient"           % "4.5.3",
  "io.spray"                  %% "spray-json"           % "1.3.3",
  "com.netaporter"            %% "scala-uri"            % "0.4.16",
  "org.scalatest"             %% "scalatest"            % "3.0.5"   % "test",
  "org.specs2"                %% "specs2-core"          % "3.8.9"   % Test,
  "org.specs2"                %% "specs2-matcher"       % "3.8.9"   % Test,
  "org.specs2"                %% "specs2-matcher-extra" % "3.8.9"   % Test,
  "org.specs2"                %% "specs2-mock"          % "3.8.9"   % Test
)