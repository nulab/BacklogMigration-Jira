package com.nulabinc.jira.client.domain

import org.joda.time.DateTime

case class Issue(
  id: Long,
  key: String,
  summary: String,
  description: Option[String],
  parent: Option[Issue],
  assignee: Option[User],
  issueFields: Seq[IssueField],
  dueDate: Option[DateTime]
)
