package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.jira.client.domain.Issue


trait IssueService {

  def countIssues(): Int

  def allIssues(startAt: Long, maxResults: Long): Seq[Issue]

  def issueOfId(id: Integer): Issue

  def tryIssueOfId(id: Integer): Either[Throwable, Issue]

}
