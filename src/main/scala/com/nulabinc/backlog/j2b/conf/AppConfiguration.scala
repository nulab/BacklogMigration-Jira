package com.nulabinc.backlog.j2b.conf

import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration

class AppConfiguration(val jiraConfig: JiraApiConfiguration,
                       val backlogConfig: BacklogApiConfiguration,
                       val importOnly: Boolean,
                       optOut: Boolean) {

  val jiraKey = jiraConfig.projectKey
  val jiraUsername = jiraConfig.username
  val jiraPassword = jiraConfig.password
  val jiraUrl      = jiraConfig.url

  val backlogKey = backlogConfig.key
  val backlogProjectKey = backlogConfig.projectKey
  val backlogUrl = backlogConfig.url

  val isOptOut = optOut
}
