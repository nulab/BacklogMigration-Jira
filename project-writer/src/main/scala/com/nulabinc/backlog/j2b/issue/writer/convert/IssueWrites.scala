package com.nulabinc.backlog.j2b.issue.writer.convert

import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain._
import com.nulabinc.backlog.migration.common.utils.DateUtil
import com.nulabinc.jira.client.domain.Issue

private [writer] class IssueWrites extends Writes[Issue, BacklogIssue] {

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
      optEstimatedHours = Option(issue.getEstimatedHours).map(_.floatValue()),
      optActualHours    = Option(issue.getSpentHours).map(_.floatValue()),
      optIssueTypeName  = Option(issue.getTracker).map(_.getName),
      statusName        = mappingStatusService.convert(issue.getStatusName),
      categoryNames     = Option(issue.getCategory).map(_.getName).toSeq,
      versionNames      = Seq.empty[String],
      milestoneNames    = Option(issue.getTargetVersion).map(_.getName).toSeq,
      priorityName      = mappingPriorityService.convert(issue.getPriorityText),
      optAssignee       = Option(issue.getAssignee).map(Convert.toBacklog(_)),
      attachments       = Seq.empty[BacklogAttachment],
      sharedFiles       = Seq.empty[BacklogSharedFile],
      customFields      = issue.getCustomFields.asScala.toSeq.flatMap(Convert.toBacklog(_)),
      notifiedUsers     = Seq.empty[BacklogUser],
      operation         = toBacklogOperation(issue)
    )

}
