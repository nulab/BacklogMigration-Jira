package com.nulabinc.backlog.j2b.conf

import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration
import com.nulabinc.backlog.migration.common.service.SpaceService
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.nulabinc.backlog4j.BacklogAPIException
import com.nulabinc.jira.client._
import com.osinka.i18n.Messages

sealed trait ConfigValidateResult
case object ConfigValidateSuccess extends ConfigValidateResult
case class ConfigValidateFailure(reason: String) extends ConfigValidateResult {
  override def toString: String = reason
}

class AppConfigValidator(jiraRestClient: JiraRestClient,
                         spaceService: SpaceService) extends Logging {

  def validate(config: AppConfiguration): List[ConfigValidateResult] = {
    List(
      AppConfigValidator.validateProjectKey(config.backlogProjectKey),
//      AppConfigValidator.validateConfigJira(jiraRestClient),
      AppConfigValidator.validateConfigBacklog(spaceService, config.backlogConfig),
      AppConfigValidator.validateJiraProject(jiraRestClient, config.jiraConfig),
      AppConfigValidator.validateAuthBacklog(spaceService)
    ).filter {
      case ConfigValidateSuccess    => false
      case ConfigValidateFailure(_) => true
    }
  }
}

object AppConfigValidator extends Logging {

  def apply(jiraRestClient: JiraRestClient,
            spaceService: SpaceService) = new AppConfigValidator(jiraRestClient, spaceService)

  def validateProjectKey(projectKey: String): ConfigValidateResult =
    if (projectKey.matches("""^[0-9A-Z_]+$""")) ConfigValidateSuccess
    else ConfigValidateFailure(s"- ${Messages("cli.param.error.project_key", projectKey)}")

  def validateConfigJira(jiraRestClient: JiraRestClient): ConfigValidateResult = {
    ConsoleOut.println(Messages("cli.param.check.access", Messages("common.src")))

    jiraRestClient.myself() match {
      case Right(_) =>
        ConsoleOut.println(Messages("cli.param.ok.access", Messages("common.src")))
        ConfigValidateSuccess
      case Left(error: HttpError) =>
        logger.error(error.message, jiraRestClient.url)
        error.clientError match {
          case AuthenticateFailedError => ConfigValidateFailure(s"- ${Messages("cli.param.error.auth", Messages("common.src"))}")
          case unknown                 => ConfigValidateFailure(s"- ${Messages("cli.param.error.client.unknown", unknown.message)}")
        }
      case Left(e) =>
        logger.error(e.message, e)
        ConfigValidateFailure(s"- ${Messages("cli.param.error.disable.access.jira", Messages("common.src"))}")
    }
  }

  def validateConfigBacklog(spaceService: SpaceService, config: BacklogApiConfiguration): ConfigValidateResult = {
    ConsoleOut.println(Messages("cli.param.check.access", Messages("common.dst")))
    try {
      spaceService.space()
      ConsoleOut.println(Messages("cli.param.ok.access", Messages("common.dst")))
      ConfigValidateSuccess
    } catch {
      case unknown: BacklogAPIException if unknown.getStatusCode == 404 =>
        logger.error(unknown.getMessage, unknown)
        ConfigValidateFailure(s"- ${Messages("cli.param.error.disable.host", Messages("common.dst"), config.url)}")
      case e: Throwable =>
        logger.error(e.getMessage, e)
        ConfigValidateFailure(s"- ${Messages("cli.param.error.disable.access.backlog", Messages("common.dst"))}")
    }
  }

  def validateJiraProject(jiraRestClient: JiraRestClient, config: JiraApiConfiguration): ConfigValidateResult =
    jiraRestClient.projectAPI.project(config.projectKey) match {
      case Right(_) => ConfigValidateSuccess
      case Left(error)  =>
        logger.error(error.message, config.projectKey)
        ConfigValidateFailure(s"- ${Messages("cli.param.error.disable.project", config.projectKey)}")
    }

  def validateAuthBacklog(spaceService: SpaceService): ConfigValidateResult = {
    ConsoleOut.println(Messages("cli.param.check.admin"))
    if (spaceService.hasAdmin()) {
      ConsoleOut.println(Messages("cli.param.ok.admin"))
      ConfigValidateSuccess
    } else ConfigValidateFailure(s"- ${Messages("cli.param.error.auth.backlog")}")
  }
}


