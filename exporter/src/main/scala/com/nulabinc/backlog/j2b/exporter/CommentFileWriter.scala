package com.nulabinc.backlog.j2b.exporter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.issue.writer.convert._
import com.nulabinc.backlog.j2b.jira.service.IssueService
import com.nulabinc.backlog.j2b.jira.writer.CommentWriter
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain.{BacklogComment, BacklogIssue}
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
    val backlogChangeLogsAsComment = changeLogs.map(Convert.toBacklog(_))
    val backlogCommentsAsComment   = comments.map(Convert.toBacklog(_))
    val backlogComments            = backlogChangeLogsAsComment ++ backlogCommentsAsComment // TODO: sort
    val reducedComments            = backlogComments.zipWithIndex.map {
      case (comment, index) =>
        exportComment(comment, backlogIssue, backlogComments, attachments, changeLogs, index)
    }
    Right(reducedComments)
  }

  private def exportComment(comment: BacklogComment,
                            issue: BacklogIssue,
                            comments: Seq[BacklogComment],
                            attachments: Seq[Attachment],
                            changeLogs: Seq[ChangeLog],
                            index: Int) = {

    import com.nulabinc.backlog.migration.common.domain.BacklogJsonProtocol._

    val commentCreated   = DateUtil.tryIsoParse(comment.optCreated)
    val issueDirPath     = backlogPaths.issueDirectoryPath("comment", issue.id, commentCreated, index)
    val changeLogReducer = new ChangeLogReducer(issueDirPath, issue, comments, attachments)
    val commentReducer   = new CommentReducer(issue.id, changeLogReducer)
    val reduced          = commentReducer.reduce(comment)

    // download
    val dir  = backlogPaths.issueAttachmentDirectoryPath(issueDirPath)
    comment.changeLogs.foreach { changeLog =>
      IOUtil.createDirectory(dir)
      changeLog.optAttachmentInfo.foreach { attachmentInfo =>
        val path = backlogPaths.issueAttachmentPath(dir, attachmentInfo.name)
        issueService.downloadAttachments(attachmentInfo.optId.getOrElse(-1), path, attachmentInfo.name)
      }
    }

    IOUtil.output(backlogPaths.issueJson(issueDirPath), reduced.toJson.prettyPrint)
    reduced
  }

}
