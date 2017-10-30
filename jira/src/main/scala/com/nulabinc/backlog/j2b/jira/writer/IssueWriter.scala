package com.nulabinc.backlog.j2b.jira.writer

import com.nulabinc.backlog.j2b.jira.service.IssueIOError
import com.nulabinc.backlog.migration.common.domain.BacklogIssue
import com.nulabinc.jira.client.domain.issue.Issue

trait IssueWriter {

  def write(issue: Issue): Either[IssueIOError, BacklogIssue]

}
