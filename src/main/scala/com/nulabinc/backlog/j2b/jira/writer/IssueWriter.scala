package com.nulabinc.backlog.j2b.jira.writer

import java.util.Date

import com.nulabinc.backlog.migration.common.domain.BacklogIssue

trait IssueWriter {

  def write(
      issue: BacklogIssue,
      issueCreatedAt: Date
  ): Either[WriteError, BacklogIssue]

}
