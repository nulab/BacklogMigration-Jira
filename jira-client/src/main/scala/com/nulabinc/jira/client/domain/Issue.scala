package com.nulabinc.jira.client.domain

case class Issue(id: String, key: String, fields: IssueField)

case class IssueField(description: Option[String])
