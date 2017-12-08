package com.nulabinc.backlog.j2b.jira.conf

import com.nulabinc.backlog.migration.common.conf.BacklogPaths

import scalax.file.Path

class JiraBacklogPaths(backlogProjectKey: String) extends BacklogPaths(backlogProjectKey) {

  def jiraUsersJson: Path = outputPath / "project" / backlogProjectKey / "jiraUsers.json"

  def jiraStatusesJson: Path = outputPath / "project" / backlogProjectKey / "jiraStatuses.json"

  def jiraPrioritiesJson: Path = outputPath / "project" / backlogProjectKey / "jiraPriorities.json"

}
