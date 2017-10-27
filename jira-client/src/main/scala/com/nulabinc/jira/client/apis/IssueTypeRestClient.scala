package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client.JiraRestClientError
import com.nulabinc.jira.client.domain.issue.IssueType

trait IssueTypeRestClient {

  def allIssueTypes(): Either[JiraRestClientError, Seq[IssueType]]

}
