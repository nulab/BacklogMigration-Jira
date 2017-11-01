package com.nulabinc.backlog.j2b.mapping.converter.writes

import javax.inject.Inject

import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain.BacklogIssue

private [converter] class IssueWrites @Inject()() extends Writes[BacklogIssue, BacklogIssue] {

  override def writes(a: BacklogIssue) = ???

}
