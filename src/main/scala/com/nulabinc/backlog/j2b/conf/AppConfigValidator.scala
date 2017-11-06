package com.nulabinc.backlog.j2b.conf

import javax.inject.Inject

import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.nulabinc.jira.client._
import com.osinka.i18n.Messages

class AppConfigValidator @Inject()(jiraRestClient: JiraRestClient) extends Logging {

  def validate(config: AppConfiguration): List[ConfigValidateResult] = {
    List(
      validateProjectKey(config.backlogProjectKey),
      validateConfigJira()
    ).filter {
      case Success()  => false
      case Failure(_) => true
    }
  }

  def validateProjectKey(projectKey: String): ConfigValidateResult =
    if (projectKey.matches("""^[0-9A-Z_]+$""")) Success()
    else Failure(s"- ${Messages("cli.param.error.project_key", projectKey)}")

  def validateConfigJira(): ConfigValidateResult = {
    ConsoleOut.println(Messages("cli.param.check.access", Messages("common.jira")))

    jiraRestClient.myself() match {
      case Right(_) => {
        ConsoleOut.println(Messages("cli.param.ok.access", Messages("common.jira")))
        Success()
      }
      case Left(error: HttpError) => {
        logger.error(error.message, jiraRestClient.url)
        error.clientError match {
          case AuthenticateFailedError => Failure(s"- ${Messages("cli.param.error.auth", Messages("common.jira"))}")
          case BadRequestError(detail) => Failure(detail) // TODO: Messages
          case unknown                 => Failure(s"- ${Messages("cli.param.error.client.unknown", unknown.message)}")
        }
      }
      case Left(e) => {
        logger.error(e.message, e)
        Failure(s"- ${Messages("cli.param.error.disable.access", Messages("common.jira"))}") // TODO: Messages
      }
    }
//    try {
//
//      None
//    } catch {
//      case auth: RedmineAuthenticationException =>
//        logger.error(auth.getMessage, auth)
//        Some(s"- ${Messages("cli.param.error.auth", Messages("common.jira"))}")
//      case noauth: NotAuthorizedException =>
//        logger.error(noauth.getMessage, noauth)
//        Some(s"- ${Messages("cli.param.error.auth.not.auth", noauth.getMessage)}")
//      case transport: RedmineTransportException =>
//        logger.error(transport.getMessage, transport)
//        Some(s"- ${Messages("cli.param.error.disable.host", Messages("common.jira"), config.redmineConfig.url)}")
//      case e: Throwable =>
//        logger.error(e.getMessage, e)
//        Some(s"- ${Messages("cli.param.error.disable.access", Messages("common.jira"))}")
//    }
  }
}
