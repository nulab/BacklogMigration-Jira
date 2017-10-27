package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.jira.client.domain.Issue


trait IssueService {

  def count(): Long

  def issues(startAt: Long, maxResults: Long): Seq[Issue]

}
