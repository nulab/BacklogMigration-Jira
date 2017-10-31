package com.nulabinc.jira.client.domain.issue

case class IssueType(
  id: Long,
  name: String,
  isSubTask: Boolean,
  description: String
)
