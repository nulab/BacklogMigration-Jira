package com.nulabinc.backlog.j2b.conf

import javax.inject.Inject

import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.nulabinc.jira.client._
import com.osinka.i18n.Messages

sealed trait ConfigValidateResult
case object ConfigValidateSuccess extends ConfigValidateResult
case class ConfigValidateFailure(reason: String) extends ConfigValidateResult


object AppConfigValidator extends Logging {

  def validateProjectKey(projectKey: String): ConfigValidateResult =
    if (projectKey.matches("""^[0-9A-Z_]+$""")) ConfigValidateSuccess
    else ConfigValidateFailure(s"- ${Messages("cli.param.error.project_key", projectKey)}")

  def validateConfigJira(jiraRestClient: JiraRestClient): ConfigValidateResult = {
    ConsoleOut.println(Messages("cli.param.check.access", Messages("common.jira")))

    jiraRestClient.myself() match {
      case Right(_) => {
        ConsoleOut.println(Messages("cli.param.ok.access", Messages("common.jira")))
        ConfigValidateSuccess
      }
      case Left(error: HttpError) => {
        logger.error(error.message, jiraRestClient.url)
        error.clientError match {
          case AuthenticateFailedError => ConfigValidateFailure(s"- ${Messages("cli.param.error.auth", Messages("common.jira"))}")
          case unknown                 => ConfigValidateFailure(s"- ${Messages("cli.param.error.client.unknown", unknown.message)}")
        }
      }
      case Left(e) => {
        logger.error(e.message, e)
        ConfigValidateFailure(s"- ${Messages("cli.param.error.disable.access", Messages("common.jira"))}")
      }
    }
  }
}

class AppConfigValidator @Inject()(jiraRestClient: JiraRestClient) extends Logging {

  def validate(config: AppConfiguration): List[ConfigValidateResult] = {
    List(
      AppConfigValidator.validateProjectKey(config.backlogProjectKey),
      AppConfigValidator.validateConfigJira(jiraRestClient)
    ).filter {
      case ConfigValidateSuccess    => false
      case ConfigValidateFailure(_) => true
    }
  }

}
