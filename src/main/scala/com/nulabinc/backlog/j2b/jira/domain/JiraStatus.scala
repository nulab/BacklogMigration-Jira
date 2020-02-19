package com.nulabinc.backlog.j2b.jira.domain

import com.nulabinc.backlog.migration.common.domain.BacklogDefaultStatus
import com.nulabinc.jira.client.domain.Status

case class JiraStatus(id: String, name: String)

object JiraStatus {

  def from(status: Status): BacklogDefaultStatus = ???
}
