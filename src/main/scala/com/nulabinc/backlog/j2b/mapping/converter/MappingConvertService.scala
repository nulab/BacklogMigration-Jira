package com.nulabinc.backlog.j2b.mapping.converter

import better.files.{File => Path}
import com.nulabinc.backlog.j2b.jira.domain.mapping.{ValidatedJiraPriorityMapping, ValidatedJiraStatusMapping, ValidatedJiraUserMapping}
import com.nulabinc.backlog.j2b.mapping.converter.writes._
import com.nulabinc.backlog.migration.common.conf.{BacklogConstantValue, BacklogPaths}
import com.nulabinc.backlog.migration.common.convert.{BacklogUnmarshaller, Convert}
import com.nulabinc.backlog.migration.common.domain.{BacklogComment, BacklogIssue}
import com.nulabinc.backlog.migration.common.formatters.BacklogJsonProtocol._
import com.nulabinc.backlog.migration.common.utils.IOUtil
import spray.json._

class MappingConvertService(backlogPaths: BacklogPaths) {

  private implicit val issueWrites: IssueWrites     = new IssueWrites()
  private implicit val commentWrites: CommentWrites = new CommentWrites()
  private implicit val userWrites: UserWrites       = new UserWrites()

  def convert(
      userMaps: Seq[ValidatedJiraUserMapping],
      priorityMaps: Seq[ValidatedJiraPriorityMapping],
      statusMaps: Seq[ValidatedJiraStatusMapping]
  ): Unit = {

    val paths: Seq[Path] =
      IOUtil.directoryPaths(backlogPaths.issueDirectoryPath)
    paths.zipWithIndex.foreach {
      case (path, _) =>
        loadDateDirectory(path, userMaps, priorityMaps, statusMaps)
    }
  }

  private def loadDateDirectory(
      path: Path,
      userMaps: Seq[ValidatedJiraUserMapping],
      priorityMaps: Seq[ValidatedJiraPriorityMapping],
      statusMaps: Seq[ValidatedJiraStatusMapping]
  ): Unit = {
    val jsonDirs = path.list.filter(_.isDirectory).toSeq
    jsonDirs.zipWithIndex.foreach {
      case (jsonDir, index) =>
        convertIssue(
          jsonDir,
          index,
          jsonDirs.size,
          userMaps,
          priorityMaps,
          statusMaps
        )
    }
  }

  private def convertIssue(
      path: Path,
      index: Int,
      size: Int,
      userMaps: Seq[ValidatedJiraUserMapping],
      priorityMaps: Seq[ValidatedJiraPriorityMapping],
      statusMaps: Seq[ValidatedJiraStatusMapping]
  ): Unit = {
    BacklogUnmarshaller.issue(backlogPaths.issueJson(path)) match {
      case Some(issue: BacklogIssue) =>
        val converted = issue.copy(
          optAssignee = issue.optAssignee.map(MappingUserConverter.convert(userMaps, _)),
          notifiedUsers = issue.notifiedUsers.map(MappingUserConverter.convert(userMaps, _)),
          operation = issue.operation.copy(
            optCreatedUser = issue.operation.optCreatedUser.map(MappingUserConverter.convert(userMaps, _)),
            optUpdatedUser = issue.operation.optUpdatedUser.map(MappingUserConverter.convert(userMaps, _))
          ),
          priorityName = MappingPriorityConverter.convert(priorityMaps, issue.priorityName),
          status = MappingStatusConverter.convert(statusMaps, issue.status)
        )
        IOUtil.output(
          backlogPaths.issueJson(path),
          Convert.toBacklog(converted).toJson.prettyPrint
        )
      case Some(comment: BacklogComment) => {
        val convertedChangeLogs = comment.changeLogs.map { changeLog =>
          changeLog.field match {
            case BacklogConstantValue.ChangeLog.STATUS =>
              changeLog.copy(
                optOriginalValue = changeLog.optOriginalValue.map(MappingStatusConverter.convert(statusMaps, _)).map(_.name.trimmed),
                optNewValue = changeLog.optNewValue.map(MappingStatusConverter.convert(statusMaps, _)).map(_.name.trimmed)
              )
            case BacklogConstantValue.ChangeLog.PRIORITY =>
              changeLog.copy(
                optOriginalValue = changeLog.optOriginalValue.map(MappingPriorityConverter.convert(priorityMaps, _)),
                optNewValue = changeLog.optNewValue.map(
                  MappingPriorityConverter.convert(priorityMaps, _)
                )
              )
            case BacklogConstantValue.ChangeLog.ASSIGNER =>
              changeLog.copy(
                optOriginalValue = changeLog.optOriginalValue.map(MappingUserConverter.convert(userMaps, _)),
                optNewValue = changeLog.optNewValue.map(
                  MappingUserConverter.convert(userMaps, _)
                )
              )
            case _ => changeLog
          }
        }
        val convertedComment = comment.copy(
          changeLogs = convertedChangeLogs,
          optCreatedUser = comment.optCreatedUser.map(
            MappingUserConverter.convert(userMaps, _)
          )
        )
        IOUtil.output(
          backlogPaths.issueJson(path),
          Convert.toBacklog(convertedComment).toJson.prettyPrint
        )
      }
      case _ =>
        throw new RuntimeException(
          s"Issue file not found.:${backlogPaths.issueJson(path).path}"
        )
    }
//    issueConsoleProgress(index + 1, size)
  }

}
