package com.nulabinc.backlog.j2b.issue.writer.convert

import javax.inject.Inject

import com.nulabinc.backlog.migration.common.convert.{Convert, Writes}
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils.{DateUtil, StringUtil}
import com.nulabinc.jira.client.domain.Comment

class CommentWrites @Inject()(implicit val userWrites: UserWrites)
    extends Writes[Comment, BacklogComment] {

  override def writes(comment: Comment) =
    BacklogComment(
      eventType = "comment",
      optIssueId = None,
      optContent = StringUtil.notEmpty(comment.body),
      changeLogs =  Seq.empty[BacklogChangeLog],
      notifications = Seq.empty[BacklogNotification],
      isCreateIssue = false,
      optCreatedUser = Some(Convert.toBacklog(comment.author)),
      optCreated = Some(DateUtil.isoFormat(comment.createdAt.toDate))
    )

}
