package com.nulabinc.backlog.j2b.issue.writer.convert

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.domain.export.IssueField
import com.nulabinc.backlog.j2b.jira.utils.SecondToHourFormatter
import com.nulabinc.backlog.migration.common.convert.{Convert, Writes}
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils.DateUtil
import com.nulabinc.jira.client.domain.issue.Issue

class IssueWrites @Inject() (
    implicit val userWrites: UserWrites,
    implicit val issueFieldWrites: IssueFieldWrites,
    implicit val attachmentWrites: AttachmentWrites
) extends Writes[(Issue, Seq[IssueField]), BacklogIssue]
    with SecondToHourFormatter {

  override def writes(map: (Issue, Seq[IssueField])) = {
    val issue = map._1
    val issueFields = map._2
    BacklogIssue(
      eventType = "issue",
      id = issue.id,
      issueKey = issue.key,
      summary =
        BacklogIssueSummary(value = issue.summary, original = issue.summary),
      optParentIssueId = issue.parent.map(_.id),
      description = issue.description.getOrElse(""),
      optStartDate = None,
      optDueDate = issue.dueDate.map(DateUtil.dateFormat),
      optEstimatedHours =
        issue.timeTrack.flatMap(_.originalEstimateSeconds.map(secondsToHours)),
      optActualHours =
        issue.timeTrack.flatMap(_.timeSpentSeconds.map(secondsToHours)),
      optIssueTypeName = Some(issue.issueType.name),
//      status = issue.status,
      status =
        BacklogCustomStatus.create(BacklogStatusName("aaa")), // TODO: fix
      categoryNames = issue.components.map(_.name),
      versionNames = issue.fixVersions.map(_.name),
      milestoneNames = Seq.empty[String],
      priorityName = issue.priority.name,
      optAssignee = issue.assignee.map(Convert.toBacklog(_)),
      attachments = issue.attachments.map(Convert.toBacklog(_)),
      sharedFiles = Seq.empty[BacklogSharedFile],
      customFields = issueFields.flatMap(Convert.toBacklog(_)),
      notifiedUsers = Seq.empty[BacklogUser],
      operation = toBacklogOperation(issue)
    )
  }

  private def toBacklogOperation(issue: Issue) =
    BacklogOperation(
      optCreatedUser = Some(Convert.toBacklog(issue.creator)),
      optCreated = Some(issue.createdAt).map(DateUtil.isoFormat),
      optUpdatedUser = issue.changeLogs.lastOption
        .flatMap(_.optAuthor.map(Convert.toBacklog(_))),
      optUpdated = Some(issue.updatedAt).map(DateUtil.isoFormat)
    )

}
