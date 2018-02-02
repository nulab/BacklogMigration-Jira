package com.nulabinc.backlog.j2b.issue.writer.convert

import javax.inject.Inject

import com.nulabinc.backlog.migration.common.convert.{Convert, Writes}
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils.DateUtil
import com.nulabinc.jira.client.domain.changeLog.ChangeLog

class ChangeLogWrites @Inject()(implicit val userWrites: UserWrites,
                                implicit val changelogItemWrites: ChangelogItemWrites)
    extends Writes[ChangeLog, BacklogComment] {

  override def writes(changeLog: ChangeLog) =
    BacklogComment(
      eventType       = "comment",
      optIssueId      = None,
      optContent      = None,
      changeLogs      = changeLog.items.map(Convert.toBacklog(_)),
      notifications   = Seq.empty[BacklogNotification],
      optCreatedUser  = Some(Convert.toBacklog(changeLog.author)),
      optCreated      = Some(DateUtil.isoFormat(changeLog.createdAt.toDate))
    )

}
