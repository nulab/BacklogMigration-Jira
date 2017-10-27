package com.nulabinc.backlog.j2b.issue.writer.convert

import com.nulabinc.backlog.migration.common.convert.{Convert, Writes}
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils.DateUtil
import com.nulabinc.jira.client.domain.issue.Issue

private [writer] class IssueWrites extends Writes[Issue, BacklogIssue] {

  override def writes(issue: Issue) = ???
//    BacklogIssue(
//      eventType         = "issue",
//      id                = issue.id,
//      optIssueKey       = None,
//      summary           = BacklogIssueSummary(value = issue.summary, original = issue.summary),
//      optParentIssueId  = issue.parent.map(_.id),
//      description       = issue.description.getOrElse(""),
//      optStartDate      = None,
//      optDueDate        = issue.dueDate.map(_.toDate).map(DateUtil.dateFormat),
//      optEstimatedHours = issue.timeTrack.originalEstimateSeconds.map(_ / 3600),
//      optActualHours    = issue.timeTrack.timeSpentSeconds.map(_ / 3600),
//      optIssueTypeName  = Some(issue.issueType.name),
//      statusName        = mappingStatusService.convert(issue.getStatusName),
//      categoryNames     = issue.components.map(_.name),
//      versionNames      = Seq.empty[String], // fixversion, version
//      milestoneNames    = Seq.empty[String],
//      priorityName      = mappingPriorityService.convert(issue.getPriorityText),
//      optAssignee       = issue.assignee.map(Convert.toBacklog(_)),
//      attachments       = Seq.empty[BacklogAttachment],
//      sharedFiles       = Seq.empty[BacklogSharedFile],
//      customFields      = issue.getCustomFields.asScala.toSeq.flatMap(Convert.toBacklog(_)),
//      notifiedUsers     = Seq.empty[BacklogUser],
//      operation         = toBacklogOperation(issue)
//    )

}
