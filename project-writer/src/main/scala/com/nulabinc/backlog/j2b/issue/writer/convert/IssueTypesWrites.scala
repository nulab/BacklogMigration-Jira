package com.nulabinc.backlog.j2b.issue.writer.convert

import javax.inject.Inject

import com.nulabinc.backlog.migration.common.conf.BacklogConstantValue
import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain.BacklogIssueType
import com.nulabinc.jira.client.domain.IssueType

private [writer] class IssueTypesWrites @Inject()() extends Writes[Seq[IssueType], Seq[BacklogIssueType]] {

  override def writes(issueTypes: Seq[IssueType]) =
    issueTypes.map(toBacklog)

  private [this] def toBacklog(issueType: IssueType) =
    BacklogIssueType(optId = Some(issueType.id),
      name = issueType.name,
      color = BacklogConstantValue.ISSUE_TYPE_COLOR.getStrValue,
      delete = false
    )

}
