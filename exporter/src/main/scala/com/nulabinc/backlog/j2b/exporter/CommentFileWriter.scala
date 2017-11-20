package com.nulabinc.backlog.j2b.exporter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.issue.writer.convert._
import com.nulabinc.backlog.j2b.jira.service.IssueService
import com.nulabinc.backlog.j2b.jira.writer.CommentWriter
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils.{DateUtil, IOUtil}
import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.changeLog.ChangeLog
import spray.json._

class CommentFileWriter @Inject()(implicit val commentWrites: CommentWrites,
                                  implicit val changeLogWrites: ChangeLogWrites,
                                  implicit val changeLogItemWrites: ChangelogItemWrites,
                                  backlogPaths: BacklogPaths,
                                  issueService: IssueService) extends CommentWriter {

  override def write(backlogIssue: BacklogIssue, comments: Seq[Comment], changeLogs: Seq[ChangeLog], attachments: Seq[Attachment]) = {
    val backlogChangeLogsAsComment = changeLogs.map(Convert.toBacklog(_))
    val backlogCommentsAsComment   = comments.map(Convert.toBacklog(_))
    val backlogComments            = backlogChangeLogsAsComment ++ backlogCommentsAsComment // TODO: sort?
    val reducedComments            = backlogComments.zipWithIndex.map {
      case (comment, index) =>
        exportComment(comment, backlogIssue, backlogComments, attachments, index)
    }
    Right(reducedComments)
  }

  private def exportComment(comment: BacklogComment,
                            issue: BacklogIssue,
                            comments: Seq[BacklogComment],
                            attachments: Seq[Attachment],
                            index: Int) = {

    import com.nulabinc.backlog.migration.common.domain.BacklogJsonProtocol._

    val commentCreated   = DateUtil.tryIsoParse(comment.optCreated)
    val issueDirPath     = backlogPaths.issueDirectoryPath("comment", issue.id, commentCreated, index)
    val changeLogReducer = new ChangeLogReducer(issueDirPath, backlogPaths, issue, comments, attachments, issueService)
    val commentReducer   = new CommentReducer(issue.id, changeLogReducer)
    val reduced          = commentReducer.reduce(comment)

    IOUtil.output(backlogPaths.issueJson(issueDirPath), reduced.toJson.prettyPrint)
    reduced
  }

}
