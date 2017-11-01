package com.nulabinc.backlog.j2b.mapping.converter.writes

import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain.BacklogIssue

private [converter] class IssueWrites extends Writes[BacklogIssue, BacklogIssue] {

  override def writes(issue: BacklogIssue) = issue

}
