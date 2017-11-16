package com.nulabinc.backlog.j2b.exporter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.issue.writer.convert._
import com.nulabinc.backlog.j2b.jira.service.IssueService
import com.nulabinc.backlog.j2b.jira.writer.CommentWriter
import com.nulabinc.backlog.migration.common.conf.{BacklogConstantValue, BacklogPaths}
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils.{DateUtil, IOUtil}
import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.changeLog.{AttachmentFieldId, ChangeLog}
import spray.json._

class CommentFileWriter @Inject()(implicit val commentWrites: CommentWrites,
                                  implicit val changeLogWrites: ChangeLogWrites,
                                  implicit val changeLogItemWrites: ChangelogItemWrites,
                                  backlogPaths: BacklogPaths,
                                  issueService: IssueService) extends CommentWriter {

  override def write(backlogIssue: BacklogIssue, comments: Seq[Comment], changeLogs: Seq[ChangeLog], attachments: Seq[Attachment]) = {

    // create changelog
    val initialAttachmentChangeLog = Seq(
      BacklogComment(
        eventType       = "comment",
        optIssueId      = Option(backlogIssue.id),
        optContent      = None,
        changeLogs      = backlogIssue.attachments.map( attachment =>
          BacklogChangeLog(
            field = BacklogConstantValue.ChangeLog.ATTACHMENT,
            optOriginalValue = None,
            optNewValue = Some(attachment.name),
            optAttachmentInfo = None,
            optAttributeInfo = None,
            optNotificationInfo = None
          )
        ),
        notifications   = Seq.empty[BacklogNotification],
        isCreateIssue   = false,
        optCreatedUser  = backlogIssue.operation.optCreatedUser,
        optCreated      = backlogIssue.operation.optCreated
      )
    )

    val backlogChangeLogsAsComment = changeLogs.map(Convert.toBacklog(_))
    val backlogCommentsAsComment   = comments.map(Convert.toBacklog(_))
    val backlogComments            = initialAttachmentChangeLog ++ backlogChangeLogsAsComment ++ backlogCommentsAsComment // TODO: sort
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
