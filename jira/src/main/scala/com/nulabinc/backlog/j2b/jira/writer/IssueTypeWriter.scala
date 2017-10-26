package com.nulabinc.backlog.j2b.jira.writer

import com.nulabinc.backlog.j2b.jira.service.IssueIOError
import com.nulabinc.backlog.migration.common.domain.BacklogIssueType
import com.nulabinc.jira.client.domain.IssueType

trait IssueTypeWriter {

  def write(issueTypes: Seq[IssueType]): Either[IssueIOError, Seq[BacklogIssueType]]

}
