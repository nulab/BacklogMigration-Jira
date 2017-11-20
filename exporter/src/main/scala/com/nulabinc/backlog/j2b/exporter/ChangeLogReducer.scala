package com.nulabinc.backlog.j2b.exporter

import com.nulabinc.backlog.j2b.jira.service.IssueService
import com.nulabinc.backlog.migration.common.conf.{BacklogConstantValue, BacklogPaths}
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.jira.client._
import com.nulabinc.jira.client.domain.Attachment
import com.osinka.i18n.Messages

import scalax.file.Path

private [exporter] class ChangeLogReducer(issueDirPath: Path,
                                          backlogPaths: BacklogPaths,
                                          issue: BacklogIssue,
                                          comments: Seq[BacklogComment],
                                          attachments: Seq[Attachment],
                                          issueService: IssueService)
  extends Logging {

  def reduce(targetComment: BacklogComment, changeLog: BacklogChangeLog): (Option[BacklogChangeLog], String) = {
    changeLog.field match {
      case BacklogConstantValue.ChangeLog.ATTACHMENT => attachment(changeLog)
      case "done_ratio" =>
        val message =
          Messages("common.change_comment", Messages("common.done_ratio"), getValue(changeLog.optOriginalValue), getValue(changeLog.optNewValue))
        (None, s"${message}\n")
      case "relates" =>
        val message =
          Messages("common.change_comment", Messages("common.relation"), getValue(changeLog.optOriginalValue), getValue(changeLog.optNewValue))
        (None, s"${message}\n")
      case "is_private" =>
        val message = Messages("common.change_comment",
          Messages("common.private"),
          getValue(privateValue(changeLog.optOriginalValue)),
          getValue(privateValue(changeLog.optNewValue)))
        (None, s"${message}\n")
      case BacklogConstantValue.ChangeLog.RESOLUTION =>
        val message = Messages("common.change_comment", Messages("common.resolution"), getValue(changeLog.optOriginalValue), getValue(changeLog.optNewValue))
        (None, s"${message}\n")
      case "timeestimate" =>
        val message = Messages("common.change_comment", Messages("common.timeestimate"), getValue(changeLog.optOriginalValue), getValue(changeLog.optNewValue))
        (None, s"${message}\n")
        // TODO: Check project
//      case "project_id" =>
//        val message = Messages("common.change_comment",
//          Messages("common.project"),
//          getProjectName(changeLog.optOriginalValue),
//          getProjectName(changeLog.optNewValue))
//        (None, s"${message}\n")
      case _ =>
        (Some(changeLog.copy(optNewValue = ValueReducer.reduce(targetComment, changeLog))), "")
    }
  }

  private[this] def getValue(optValue: Option[String]): String = {
    optValue.getOrElse(Messages("common.empty"))
  }

//  private[this] def getProjectName(optValue: Option[String]): String = {
//    optValue match {
//      case Some(value) =>
//        StringUtil.safeStringToInt(value) match {
//          case Some(intValue) => exportContext.projectService.optProjectOfId(intValue).map(_.getName).getOrElse(Messages("common.empty"))
//          case _              => Messages("common.empty")
//        }
//      case _ => Messages("common.empty")
//    }
//  }

  private[this] def privateValue(optValue: Option[String]): Option[String] = {
    optValue match {
      case Some("0") => Some(Messages("common.no"))
      case Some("1") => Some(Messages("common.yes"))
      case _         => None
    }
  }

  private def attachment(changeLog: BacklogChangeLog): (Option[BacklogChangeLog], String) = {
    changeLog.optAttachmentInfo match {
      case Some(attachmentInfo) =>
        attachmentInfo.optId match {
          case Some(attachmentInfoId) =>
            // download
            val dir  = backlogPaths.issueAttachmentDirectoryPath(issueDirPath)
            val path = backlogPaths.issueAttachmentPath(dir, attachmentInfo.name)
            issueService.downloadAttachments(attachmentInfoId.toLong, path, attachmentInfo.name) match {
              case Success =>
                (Some(changeLog), "")
              case Failure =>
                val emptyMessage = Messages(
                  "export.attachment.empty",
                  changeLog.optOriginalValue.getOrElse(Messages("common.empty")),
                  changeLog.optNewValue.getOrElse(Messages("common.empty"))
                )
                (None, s"$emptyMessage\n")
            }
          case _ =>
            val emptyMessage = Messages(
              "export.attachment.empty",
              changeLog.optOriginalValue.getOrElse(Messages("common.empty")),
              changeLog.optNewValue.getOrElse(Messages("common.empty"))
            )
            (None, s"$emptyMessage\n")
        }
      case _ =>
        val emptyMessage = Messages(
          "export.attachment.empty",
          changeLog.optOriginalValue.getOrElse(Messages("common.empty")),
          changeLog.optNewValue.getOrElse(Messages("common.empty"))
        )
        (None, s"$emptyMessage\n")
    }
  }

  object ValueReducer {
    def reduce(targetComment: BacklogComment, changeLog: BacklogChangeLog): Option[String] = {
      changeLog.field match {
//        case BacklogConstantValue.ChangeLog.VERSION | BacklogConstantValue.ChangeLog.MILESTONE =>
//          findProperty(comments)(changeLog.field) match {
//            case Some(lastComment) if lastComment.optCreated == targetComment.optCreated =>
//              changeLog.field match {
//                case BacklogConstantValue.ChangeLog.VERSION =>
//                  val issueValue = issue.versionNames.mkString(", ")
//                  if (issueValue.trim.isEmpty) changeLog.optNewValue else Some(issueValue)
//                case BacklogConstantValue.ChangeLog.MILESTONE =>
//                  val issueValue = issue.milestoneNames.mkString(", ")
//                  if (issueValue.trim.isEmpty) changeLog.optNewValue else Some(issueValue)
//                case BacklogConstantValue.ChangeLog.ISSUE_TYPE =>
//                  val issueValue = issue.optIssueTypeName.getOrElse("")
//                  if (issueValue.trim.isEmpty) changeLog.optNewValue else Some(issueValue)
//                case _ => throw new RuntimeException
//              }
//            case _ => changeLog.optNewValue
//          }
        case _ => changeLog.optNewValue
      }
    }
  }

}