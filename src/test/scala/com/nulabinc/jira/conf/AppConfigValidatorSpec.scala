package com.nulabinc.jira.conf

import com.nulabinc.backlog.j2b.conf.{AppConfigValidator, ConfigValidateSuccess}
import org.scalatest.{DiagrammedAssertions, FlatSpec}

class AppConfigValidatorSpec extends FlatSpec with DiagrammedAssertions {

  "AppConfigValidator.validateProjectKey" should "only accept A-Z 0-9 _" in {
    assert(
      AppConfigValidator.validateProjectKey("PROJECT") === ConfigValidateSuccess
    )
    assert(
      AppConfigValidator.validateProjectKey("UI+09") !== ConfigValidateSuccess
    )
  }
}
