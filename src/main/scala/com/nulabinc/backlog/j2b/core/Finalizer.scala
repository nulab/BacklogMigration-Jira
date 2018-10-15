package com.nulabinc.backlog.j2b.core

import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.migration.common.utils.ControlUtil.using
import com.nulabinc.backlog.migration.common.utils.Logging

object Finalizer extends Logging {

  def finalize(config: AppConfiguration): Unit = {
    import java.net._

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

    val url = new URL(s"${config.backlogUrl}/api/v2/importer/jira?projectKey=${config.backlogProjectKey}")
    url.openConnection match {
      case http: HttpURLConnection =>
        http.setRequestMethod("GET")
        http.connect()
        using(http) { connection =>
          connection.getResponseCode
        }
      case _ => 0
    }
  }
}
