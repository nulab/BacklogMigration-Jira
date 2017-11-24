package com.nulabinc.backlog.j2b.jira.conf

import com.nulabinc.backlog.migration.common.conf.BacklogPaths

import scalax.file.Path

class JiraBacklogPaths(jiraProjectKey: String, backlogProjectKey: String) extends BacklogPaths(backlogProjectKey) {

  def jiraUsersJson: Path = outputPath / "project" / jiraProjectKey / "jiraUsers.json"

  def jiraStatusesJson: Path = outputPath / "project" / jiraProjectKey / "jiraStatuses.json"

  def jiraPrioritiesJson: Path = outputPath / "project" / jiraProjectKey / "jiraPriorities.json"

}
