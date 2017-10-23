package com.nulabinc.backlog.j2b.jira.writer

import com.nulabinc.backlog.j2b.jira.service.IssueIOError
import com.nulabinc.backlog.migration.common.domain.BacklogIssueCategory
import com.nulabinc.jira.client.domain.Component

trait IssueCategoriesWriter {

  def write(categories: Seq[Component]): Either[IssueIOError, Seq[BacklogIssueCategory]]

}
