package com.nulabinc.backlog.j2b.jira.writer

import com.nulabinc.backlog.migration.common.domain.BacklogIssueCategory
import com.nulabinc.jira.client.domain.Component

trait ComponentWriter {

  def write(categories: Seq[Component]): Either[WriteError, Seq[BacklogIssueCategory]]

}
