package com.nulabinc.jira.client.domain.issue

import com.nulabinc.jira.client.domain.{Component, User}
import org.joda.time.DateTime

case class Issue(
  id: Long,
  key: String,
  summary: String,
  description: Option[String],
  parent: Option[Issue],
  assignee: Option[User],
  components: Seq[Component],
  issueFields: Seq[IssueField],
  dueDate: Option[DateTime],
  timeTrack: TimeTrack,
  issueType: IssueType
)
