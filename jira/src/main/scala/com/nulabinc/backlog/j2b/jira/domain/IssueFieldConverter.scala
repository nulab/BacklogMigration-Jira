package com.nulabinc.backlog.j2b.jira.domain

import com.nulabinc.backlog.j2b.jira.domain.export.{IssueField => ExportIssueField}
import com.nulabinc.jira.client.domain.issue.{IssueField => ClientIssueField}

object IssueFieldConverter {

  def toExportIssueFields(clientIssueFields: Seq[ClientIssueField]): Seq[ExportIssueField] = ???
}
