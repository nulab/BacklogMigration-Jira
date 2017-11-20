package com.nulabinc.jira.client.domain.issue

import java.util.Date

import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.changeLog.ChangeLog
import org.joda.time.DateTime

case class Issue(
  id: Long,
  key: String,
  summary: String,
  description: Option[String],
  parent: Option[ParentIssue],
  assignee: Option[User],
  components: Seq[Component],
  fixVersions: Seq[Version],
  issueFields: Seq[IssueField],
  dueDate: Option[Date],
  timeTrack: Option[TimeTrack],
  issueType: IssueType,
  status: Status,
  priority: Priority,
  creator: User,
  createdAt: DateTime,
  updatedAt: DateTime,
  changeLogs: Seq[ChangeLog],
  attachments: Seq[Attachment]
) {

  def injectChangeLogs(changeLogs: Seq[ChangeLog]) =
    this.copy(changeLogs = changeLogs)

  def injectAttachments(attachments: Seq[Attachment]) =
    this.copy(attachments = attachments)
}

case class ParentIssue(id: Long)
