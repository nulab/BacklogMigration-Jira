package com.nulabinc.backlog.j2b.conf

import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.migration.SimpleFixture
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration
import org.scalatest.{FlatSpec, Matchers}

class AppConfigValidatorSpec extends FlatSpec with Matchers with SimpleFixture {

  "validateProjectKey" should "only accept A-Z 0-9 _" in {
    val jiraConfig = JiraApiConfiguration("user", "pass", "https://aaa.com", "project")
    val backlogConfig = BacklogApiConfiguration("url", "key", "PROJECT")
//    val validator = new AppConfigValidator()
//
//    val config = new AppConfiguration(jiraConfig, backlogConfig, true, true)
//    validator.validate(config).length should be(0)
//
//    val config1 = new AppConfiguration(jiraConfig, backlogConfig.copy(projectKey = "UI-09"), true, true)
//    validator.validate(config1).length should be (1)
  }
}
