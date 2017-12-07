package com.nulabinc.backlog.j2b.jira.writer

import com.nulabinc.backlog.j2b.jira.domain.export.Milestone
import com.nulabinc.backlog.migration.common.domain.BacklogVersion
import com.nulabinc.jira.client.domain.Version

trait VersionWriter {

  def write(versions: Seq[Version], milestones: Seq[Milestone]): Either[WriteError, Seq[BacklogVersion]]

}
