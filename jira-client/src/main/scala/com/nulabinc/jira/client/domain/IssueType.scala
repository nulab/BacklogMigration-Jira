package com.nulabinc.jira.client.domain

case class IssueType(
  id: Long,
  name: String,
  isSubTask: Boolean,
  description: String
)
