package com.nulabinc.backlog.j2b.jira.writer

import com.nulabinc.backlog.j2b.jira.service.IssueIOError
import com.nulabinc.backlog.migration.common.domain.BacklogVersion
import com.nulabinc.jira.client.domain.Version

trait VersionWriter {

  def write(versions: Seq[Version]): Either[IssueIOError, Seq[BacklogVersion]]

}
