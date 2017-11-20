package com.nulabinc.backlog.j2b.mapping.converter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.converter._
import com.nulabinc.backlog.j2b.jira.domain.mapping.Mapping
import com.nulabinc.backlog.j2b.mapping.converter.writes._
import com.nulabinc.backlog.migration.common.conf.{BacklogConstantValue, BacklogPaths}
import com.nulabinc.backlog.migration.common.convert.{BacklogUnmarshaller, Convert}
import com.nulabinc.backlog.migration.common.domain.{BacklogComment, BacklogIssue}
import com.nulabinc.backlog.migration.common.utils.IOUtil
import com.nulabinc.backlog.migration.common.domain.BacklogJsonProtocol._
import spray.json._

import scalax.file.Path

class MappingConvertService @Inject()(implicit val issueWrites: IssueWrites,
                                      implicit val commentWrites: CommentWrites,
                                      userConverter: UserConverter,
                                      priorityConverter: PriorityConverter,
                                      statusConverter: StatusConverter,
                                      backlogPaths: BacklogPaths) extends MappingConverter {


  def convert(userMaps: Seq[Mapping], priorityMaps: Seq[Mapping], statusMaps: Seq[Mapping]): Unit = {

    val paths: Seq[Path] = IOUtil.directoryPaths(backlogPaths.issueDirectoryPath)
      .flatMap(_.toAbsolute.children().filter(_.isDirectory).toSeq)
    paths.zipWithIndex.foreach {
      case (path, index) =>
        convertIssue(path, index, paths.size, userMaps, priorityMaps, statusMaps)
    }
  }

  private def convertIssue(path: Path, index: Int, size: Int, userMaps: Seq[Mapping], priorityMaps: Seq[Mapping], statusMaps: Seq[Mapping]) = {
    BacklogUnmarshaller.issue(backlogPaths.issueJson(path)) match {
      case Some(issue: BacklogIssue) => {
        val converted = issue.copy(
          optAssignee = issue.optAssignee.map(userConverter.convert(userMaps, _)),
          notifiedUsers = issue.notifiedUsers.map(userConverter.convert(userMaps, _)),
          operation = issue.operation.copy(
            optCreatedUser = issue.operation.optCreatedUser.map(userConverter.convert(userMaps, _)),
            optUpdatedUser = issue.operation.optUpdatedUser.map(userConverter.convert(userMaps, _))
          ),
          priorityName = priorityConverter.convert(priorityMaps, issue.priorityName),
          statusName = statusConverter.convert(statusMaps, issue.statusName)
        )
        IOUtil.output(backlogPaths.issueJson(path), Convert.toBacklog(converted).toJson.prettyPrint)
      }
      case Some(comment: BacklogComment) => {
        val convertedChangeLogs = comment.changeLogs.map { changeLog =>
          changeLog.field match {
            case BacklogConstantValue.ChangeLog.STATUS => changeLog.copy(
              optOriginalValue = changeLog.optOriginalValue.map(statusConverter.convert(statusMaps, _)),
              optNewValue      = changeLog.optNewValue.map(statusConverter.convert(statusMaps, _))
            )
            case BacklogConstantValue.ChangeLog.PRIORITY => changeLog.copy(
              optOriginalValue = changeLog.optOriginalValue.map(priorityConverter.convert(priorityMaps, _)),
              optNewValue      = changeLog.optNewValue.map(priorityConverter.convert(priorityMaps, _))
            )
            case BacklogConstantValue.ChangeLog.ASSIGNER => changeLog.copy(
              optOriginalValue = changeLog.optOriginalValue.map(userConverter.convert(userMaps, _)),
              optNewValue      = changeLog.optNewValue.map(userConverter.convert(userMaps, _))
            )
            case _ => changeLog
          }
        }
        IOUtil.output(
          backlogPaths.issueJson(path),
          Convert.toBacklog(
            comment.copy(changeLogs = convertedChangeLogs)
          ).toJson.prettyPrint
        )
      }
      case _ => throw new RuntimeException(s"Issue file not found.:${backlogPaths.issueJson(path).path}")
    }
//    issueConsoleProgress(index + 1, size)
  }

}
