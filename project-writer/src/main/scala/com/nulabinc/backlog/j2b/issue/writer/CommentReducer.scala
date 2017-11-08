package com.nulabinc.backlog.j2b.issue.writer

import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils.{Logging, StringUtil}

private [writer] class CommentReducer(issueId: Long, changeLogReducer: ChangeLogReducer) extends Logging {

  def reduce(comment: BacklogComment): BacklogComment = {
    val changeLogContent = new StringBuilder()
    val newChangeLogs = comment.changeLogs.flatMap { changeLog =>
      val (optNewChangeLog, addingContent) = changeLogReducer.reduce(comment, changeLog)
      changeLogContent.append(addingContent)
      optNewChangeLog
    }
    val optNewContent = comment.optContent match {
      case Some(content) =>
        val newContent = (s"${changeLogContent.result()}\n${content}").trim
        StringUtil.notEmpty(newContent)
      case None =>
        StringUtil.notEmpty(changeLogContent.result().trim)
    }
    comment.copy(optIssueId = Some(issueId), optContent = optNewContent, isCreateIssue = false, changeLogs = newChangeLogs)
  }

}
