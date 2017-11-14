package com.nulabinc.backlog.j2b.issue.writer.convert

import javax.inject.Inject

import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain.BacklogVersion
import com.nulabinc.backlog.migration.common.utils.DateUtil
import com.nulabinc.jira.client.domain.Version

private [writer] class VersionWrites @Inject()() extends Writes[Seq[Version], Seq[BacklogVersion]] {

  override def writes(versions: Seq[Version]) =
    versions.map(toBacklog)

  private[this] def toBacklog(version: Version) = {
    BacklogVersion(
      optId             = version.id,
      name              = version.name,
      description       = version.description.getOrElse(""),
      optStartDate      = None,
      optReleaseDueDate = version.releaseDate.map(_.toDate).map(DateUtil.dateFormat),
      delete            = false
    )
  }
}
