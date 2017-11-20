package com.nulabinc.backlog.j2b.cli

import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.migration.common.service.ProjectService
import com.nulabinc.backlog.migration.common.utils.Logging
import com.osinka.i18n.Messages

trait InteractiveConfirm extends Logging {

  def confirmProject(config: AppConfiguration, projectService: ProjectService): Option[(String, String)] =
    projectService.optProject(config.backlogConfig.projectKey) match {
      case Some(_) =>
        val input: String = scala.io.StdIn.readLine(Messages("cli.backlog_project_already_exist", config.backlogConfig.projectKey))
        if (input == "y" || input == "Y") Some((config.jiraConfig.projectKey, config.backlogConfig.projectKey))
        else None
      case None =>
        Some((config.jiraConfig.projectKey, config.backlogConfig.projectKey))
    }
}
