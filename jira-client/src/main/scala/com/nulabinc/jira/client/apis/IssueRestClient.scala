package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client.JiraRestClientError
import com.nulabinc.jira.client.domain.Issue

trait IssueRestClient {

  def issue(id: Long): Either[JiraRestClientError, Issue]

  def issue(key: String): Either[JiraRestClientError, Issue]

  def projectIssues(key: String, startAt: Long, maxResults: Long): Either[JiraRestClientError, Seq[Issue]]
}
