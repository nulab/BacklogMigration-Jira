package com.nulabinc.jira.client.domain

case class Issue(id: Long, key: String, fields: IssueField)

case class IssueField(description: Option[String])
