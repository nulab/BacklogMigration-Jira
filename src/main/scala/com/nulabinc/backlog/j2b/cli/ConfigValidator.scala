package com.nulabinc.backlog.j2b.cli

import com.nulabinc.backlog.j2b.conf._
import com.nulabinc.backlog.migration.common.service.SpaceService
import com.nulabinc.backlog.migration.common.utils._
import com.nulabinc.jira.client.JiraRestClient
import com.osinka.i18n.Messages

trait ConfigValidator extends Logging {

  def validateConfig(config: AppConfiguration,
                     jiraRestClient: JiraRestClient,
                     spaceService: SpaceService): Option[Unit] = {

    val validator = AppConfigValidator(jiraRestClient, spaceService)
    val errors    = validator.validate(config)
    if (errors.isEmpty) Some(())
    else {
      val message =
        s"""
           |
           |${Messages("cli.param.error")}
           |--------------------------------------------------
           |${errors.mkString("\n")}
           |
        """.stripMargin
      ConsoleOut.error(message)
      None
    }
  }
}
