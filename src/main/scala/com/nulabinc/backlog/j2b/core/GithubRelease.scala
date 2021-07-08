package com.nulabinc.backlog.j2b.core

import com.nulabinc.backlog.migration.common.utils.Logging

object GithubRelease extends Logging {

  def checkRelease(): String = {
    import java.io._
    import java.net._
    import spray.json._
    import spray.json.DefaultJsonProtocol._

    val url = new URL(
      "https://api.github.com/repos/nulab/BacklogMigration-Jira/releases"
    )
    val http = url.openConnection().asInstanceOf[HttpURLConnection]
    val optProxyUser = Option(System.getProperty("https.proxyUser"))
    val optProxyPass = Option(System.getProperty("https.proxyPassword"))

    (optProxyUser, optProxyPass) match {
      case (Some(proxyUser), Some(proxyPass)) =>
        Authenticator.setDefault(new Authenticator() {
          override def getPasswordAuthentication: PasswordAuthentication = {
            new PasswordAuthentication(proxyUser, proxyPass.toCharArray)
          }
        })
      case _ => ()
    }

    try {
      http.setRequestMethod("GET")
      http.connect()

      val reader = new BufferedReader(
        new InputStreamReader(http.getInputStream)
      )
      val output = new StringBuilder()
      var line = ""

      while (line != null) {
        line = reader.readLine()
        if (line != null)
          output.append(line)
      }
      reader.close()

      output.toString().parseJson match {
        case JsArray(releases) if releases.nonEmpty =>
          releases(0).asJsObject.fields
            .apply("tag_name")
            .convertTo[String]
            .replace("v", "")
        case _ => ""
      }
    } catch {
      case ex: Throwable =>
        logger.error(ex.getMessage, ex)
        ""
    }
  }
}
