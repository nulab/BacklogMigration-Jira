package com.nulabinc.backlog.j2b.issue.writer.convert

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.utils.SecondToHourFormatter
import com.nulabinc.backlog.migration.common.convert.{Convert, Writes}
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils.DateUtil
import com.nulabinc.jira.client.domain.issue.Issue

class IssueWrites @Inject()(implicit val userWrites: UserWrites,
                            implicit val issueFieldWrites: IssueFieldWrites,
                            implicit val attachmentWrites: AttachmentWrites)
    extends Writes[Issue, BacklogIssue]
    with SecondToHourFormatter {

  override def writes(issue: Issue) =
    BacklogIssue(
      eventType         = "issue",
      id                = issue.id,
      optIssueKey       = None,
      summary           = BacklogIssueSummary(value = issue.summary, original = issue.summary),
      optParentIssueId  = issue.parent.map(_.id),
      description       = issue.description.getOrElse(""),
      optStartDate      = None,
      optDueDate        = issue.dueDate.map(DateUtil.dateFormat),
      optEstimatedHours = issue.timeTrack.flatMap(_.originalEstimateSeconds.map(secondsToHours)),
      optActualHours    = issue.timeTrack.flatMap(_.timeSpentSeconds.map(secondsToHours)),
      optIssueTypeName  = Some(issue.issueType.name),
      statusName        = issue.status.name,
      categoryNames     = issue.components.map(_.name),
      versionNames      = issue.fixVersions.map(_.name),
      milestoneNames    = Seq.empty[String],
      priorityName      = issue.priority.name,
      optAssignee       = issue.assignee.map(Convert.toBacklog(_)),
      attachments       = issue.attachments.map(Convert.toBacklog(_)),
      sharedFiles       = Seq.empty[BacklogSharedFile],
      customFields      = issue.issueFields.flatMap(Convert.toBacklog(_)),
      notifiedUsers     = Seq.empty[BacklogUser],
      operation         = toBacklogOperation(issue)
    )

  private def toBacklogOperation(issue: Issue) =
    BacklogOperation(
      optCreatedUser  = Some(Convert.toBacklog(issue.creator)),
      optCreated      = Some(issue.createdAt.toDate).map(DateUtil.isoFormat),
      optUpdatedUser  = issue.changeLogs.lastOption.map { u => Convert.toBacklog(u.author) },
      optUpdated      = Some(issue.updatedAt.toDate).map(DateUtil.isoFormat)
    )

}
