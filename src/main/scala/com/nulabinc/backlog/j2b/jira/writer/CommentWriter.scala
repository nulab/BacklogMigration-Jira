package com.nulabinc.backlog.j2b.jira.writer

import com.nulabinc.backlog.migration.common.domain.{BacklogComment, BacklogIssue}
import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.changeLog.ChangeLog

trait CommentWriter {

  def write(
      backlogIssue: BacklogIssue,
      comments: Seq[Comment],
      changeLogs: Seq[ChangeLog],
      attachments: Seq[Attachment]
  ): Either[WriteError, Seq[BacklogComment]]

}
