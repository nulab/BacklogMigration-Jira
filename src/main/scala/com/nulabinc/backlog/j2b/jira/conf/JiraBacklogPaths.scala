package com.nulabinc.backlog.j2b.jira.conf

import better.files.{File => BetterFile}
import com.nulabinc.backlog.migration.common.conf.BacklogPaths

class JiraBacklogPaths(backlogProjectKey: String) extends BacklogPaths(backlogProjectKey) {

  def jiraUsersJson: BetterFile =
    outputPath / "project" / backlogProjectKey / "jiraUsers.json"

  def jiraPrioritiesJson: BetterFile =
    outputPath / "project" / backlogProjectKey / "jiraPriorities.json"

}
