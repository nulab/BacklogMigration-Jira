package com.nulabinc.backlog.j2b.conf

import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration

class AppConfiguration(jiraConfig: JiraApiConfiguration,
                       backlogConfig: BacklogApiConfiguration,
                       val importOnly: Boolean,
                       optOut: Boolean) {

  val jiraUsername = jiraConfig.username
  val jiraPassword = jiraConfig.password

  val backlogKey = backlogConfig.key
  val backlogProjectKey = backlogConfig.projectKey
  val backlogUrl = backlogConfig.url

  val isOptOut = optOut
}
