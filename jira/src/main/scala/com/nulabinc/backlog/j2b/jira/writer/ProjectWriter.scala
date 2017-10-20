package com.nulabinc.backlog.j2b.jira.writer

import com.nulabinc.backlog.j2b.jira.service.IssueIOError
import com.nulabinc.backlog.migration.common.domain.BacklogProject
import com.nulabinc.jira.client.domain.Project

trait ProjectWriter {

  def write(project: Project): Either[IssueIOError, BacklogProject]

}
