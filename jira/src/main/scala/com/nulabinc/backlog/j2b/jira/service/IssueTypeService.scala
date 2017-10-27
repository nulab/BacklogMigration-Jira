package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.jira.client.domain.IssueType

trait IssueTypeService {

  def all(): Seq[IssueType]
}
