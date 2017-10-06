package com.nulabinc.backlog.j2b.conf

import com.nulabinc.backlog.migration.common.utils.Logging
import com.osinka.i18n.Messages

class AppConfigValidator() extends Logging {

  def validate(config: AppConfiguration): List[ConfigValidateResult] = {
    List(
      validateProjectKey(config.backlogProjectKey)
    ).filter {
      case Success()  => false
      case Failure(_) => true
    }
  }

  def validateProjectKey(projectKey: String): ConfigValidateResult =
    if (projectKey.matches("""^[0-9A-Z_]+$""")) Success()
    else Failure(s"- ${Messages("cli.param.error.project_key", projectKey)}")
}
