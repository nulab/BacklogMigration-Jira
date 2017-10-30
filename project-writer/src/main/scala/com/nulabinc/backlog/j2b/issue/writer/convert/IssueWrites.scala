package com.nulabinc.backlog.j2b.issue.writer.convert

import javax.inject.Inject

import com.nulabinc.backlog.migration.common.convert.{Convert, Writes}
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils.DateUtil
import com.nulabinc.jira.client.domain.issue.Issue

private [writer] class IssueWrites @Inject()(implicit val userWrites: UserWrites,
                                             implicit val issueFieldWrites: IssueFieldWrites)
    extends Writes[Issue, BacklogIssue] {

  override def writes(issue: Issue) =
    BacklogIssue(
      eventType         = "issue",
      id                = issue.id,
      optIssueKey       = None,
      summary           = BacklogIssueSummary(value = issue.summary, original = issue.summary),
      optParentIssueId  = issue.parent.map(_.id),
      description       = issue.description.getOrElse(""),
      optStartDate      = None,
      optDueDate        = issue.dueDate.map(_.toDate).map(DateUtil.dateFormat),
      optEstimatedHours = issue.timeTrack.flatMap(_.originalEstimateSeconds.map(_ / 3600f)),
      optActualHours    = issue.timeTrack.flatMap(_.timeSpentSeconds.map(_ / 3600f)),
      optIssueTypeName  = Some(issue.issueType.name),
      statusName        = issue.status.name,
      categoryNames     = issue.components.map(_.name),
      versionNames      = Seq.empty[String], // fixversion, version
      milestoneNames    = Seq.empty[String], // TODO: check
      priorityName      = issue.priority.name,
      optAssignee       = issue.assignee.map(Convert.toBacklog(_)),
      attachments       = Seq.empty[BacklogAttachment],   // TODO: attachments
      sharedFiles       = Seq.empty[BacklogSharedFile],   // TODO: sharedfiles
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
