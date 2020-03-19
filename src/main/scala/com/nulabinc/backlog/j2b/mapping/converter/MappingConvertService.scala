package com.nulabinc.backlog.j2b.mapping.converter

import better.files.{File => Path}
import com.nulabinc.backlog.j2b.jira.converter._
import com.nulabinc.backlog.j2b.jira.domain.mapping.{ValidatedJiraStatusMapping, Mapping, MappingCollectDatabase}
import com.nulabinc.backlog.j2b.mapping.converter.writes._
import com.nulabinc.backlog.migration.common.conf.{BacklogConstantValue, BacklogPaths}
import com.nulabinc.backlog.migration.common.convert.{BacklogUnmarshaller, Convert}
import com.nulabinc.backlog.migration.common.domain.{BacklogComment, BacklogIssue}
import com.nulabinc.backlog.migration.common.formatters.BacklogJsonProtocol._
import com.nulabinc.backlog.migration.common.utils.IOUtil
import javax.inject.Inject
import spray.json._

class MappingConvertService @Inject()(implicit val issueWrites: IssueWrites,
                                      implicit val commentWrites: CommentWrites,
                                      implicit val userWrites: UserWrites,
                                      priorityConverter: PriorityConverter,
                                      backlogPaths: BacklogPaths) extends MappingConverter {

  private val userConverter = new MappingUserConverter()

  def convert(database: MappingCollectDatabase, userMaps: Seq[Mapping], priorityMaps: Seq[Mapping], statusMaps: Seq[ValidatedJiraStatusMapping]): Unit = {

    val paths: Seq[Path] = IOUtil.directoryPaths(backlogPaths.issueDirectoryPath)
    paths.zipWithIndex.foreach {
      case (path, _) =>
        loadDateDirectory(path, database, userMaps, priorityMaps, statusMaps)
    }
  }

  private def loadDateDirectory(path: Path, database: MappingCollectDatabase, userMaps: Seq[Mapping], priorityMaps: Seq[Mapping], statusMaps: Seq[ValidatedJiraStatusMapping]): Unit = {
    val jsonDirs = path.list.filter(_.isDirectory).toSeq
    jsonDirs.zipWithIndex.foreach {
      case (jsonDir, index) =>
        convertIssue(database, jsonDir, index, jsonDirs.size, userMaps, priorityMaps, statusMaps)
    }
  }

  private def convertIssue(database: MappingCollectDatabase, path: Path, index: Int, size: Int, userMaps: Seq[Mapping], priorityMaps: Seq[Mapping], statusMaps: Seq[ValidatedJiraStatusMapping]): Unit = {
    BacklogUnmarshaller.issue(backlogPaths.issueJson(path)) match {
      case Some(issue: BacklogIssue) =>
        val converted = issue.copy(
          optAssignee = issue.optAssignee.map(userConverter.convert(database, userMaps, _)),
          notifiedUsers = issue.notifiedUsers.map(userConverter.convert(database, userMaps, _)),
          operation = issue.operation.copy(
            optCreatedUser = issue.operation.optCreatedUser.map(userConverter.convert(database, userMaps, _)),
            optUpdatedUser = issue.operation.optUpdatedUser.map(userConverter.convert(database, userMaps, _))
          ),
          priorityName = priorityConverter.convert(priorityMaps, issue.priorityName),
          status = MappingStatusConverter.convert(statusMaps, issue.status)
        )
        IOUtil.output(backlogPaths.issueJson(path), Convert.toBacklog(converted).toJson.prettyPrint)
      case Some(comment: BacklogComment) => {
        val convertedChangeLogs = comment.changeLogs.map { changeLog =>
          changeLog.field match {
            case BacklogConstantValue.ChangeLog.STATUS => changeLog.copy(
              optOriginalValue = changeLog.optOriginalValue.map(MappingStatusConverter.convert(statusMaps, _)).map(_.name.trimmed),
              optNewValue      = changeLog.optNewValue.map(MappingStatusConverter.convert(statusMaps, _)).map(_.name.trimmed)
            )
            case BacklogConstantValue.ChangeLog.PRIORITY => changeLog.copy(
              optOriginalValue = changeLog.optOriginalValue.map(priorityConverter.convert(priorityMaps, _)),
              optNewValue      = changeLog.optNewValue.map(priorityConverter.convert(priorityMaps, _))
            )
            case BacklogConstantValue.ChangeLog.ASSIGNER => changeLog.copy(
              optOriginalValue = changeLog.optOriginalValue.map(userConverter.convert(database, userMaps, _)),
              optNewValue      = changeLog.optNewValue.map(userConverter.convert(database, userMaps, _))
            )
            case _ => changeLog
          }
        }
        val convertedComment = comment.copy(
          changeLogs = convertedChangeLogs,
          optCreatedUser = comment.optCreatedUser.map(userConverter.convert(database, userMaps, _))
        )
        IOUtil.output(backlogPaths.issueJson(path), Convert.toBacklog(convertedComment).toJson.prettyPrint)
      }
      case _ => throw new RuntimeException(s"Issue file not found.:${backlogPaths.issueJson(path).path}")
    }
//    issueConsoleProgress(index + 1, size)
  }

}
