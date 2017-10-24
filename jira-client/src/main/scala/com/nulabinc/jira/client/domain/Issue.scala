package com.nulabinc.jira.client.domain

case class Issue(
  id: Long,
  key: String,
  description: Option[String],
  assignee: Option[User],
  issueFields: Seq[IssueField]
)
