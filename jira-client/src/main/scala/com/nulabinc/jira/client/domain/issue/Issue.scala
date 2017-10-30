package com.nulabinc.jira.client.domain.issue

import com.nulabinc.jira.client.domain._
import org.joda.time.DateTime

case class Issue(
  id: Long,
  key: String,
  summary: String,
  description: Option[String],
  parent: Option[Issue],
  assignee: Option[User],
  components: Seq[Component],
  issueFields: Seq[IssueField],
  dueDate: Option[DateTime],
  timeTrack: Option[TimeTrack],
  issueType: IssueType,
  status: Status,
  priority: Priority,
  creator: User,
  createdAt: DateTime,
  updatedAt: DateTime,
  changeLogs: Seq[ChangeLog]
) {

  def injectChangeLogs(changeLogs: Seq[ChangeLog]) =
    this.copy(changeLogs = changeLogs)
}
