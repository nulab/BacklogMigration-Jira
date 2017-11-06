package com.nulabinc.backlog.j2b.exporter.service

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.j2b.jira.service.IssueService
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.jira.client.JiraRestClient
import com.nulabinc.jira.client.domain.issue.Issue

class JiraClientIssueService @Inject()(apiConfig: JiraApiConfiguration, projectKey: JiraProjectKey, jira: JiraRestClient)
    extends IssueService with Logging {

  override def count() = {
    jira.searchAPI.searchJql(s"project=${projectKey.value}", 0, 0) match {
      case Right(result) => result.total
      case Left(error) => {
        logger.error(error.message)
        0
      }
    }
  }

  override def issues(startAt: Long, maxResults: Long) =
    jira.issueAPI.projectIssues(projectKey.value, startAt, maxResults) match {
      case Right(result) => result
      case Left(error) => {
        logger.error(error.message)
        Seq.empty[Issue]
      }
    }

  override def injectChangeLogsToIssue(issue: Issue) = {
    val changeLogs = jira.issueAPI.changeLogs(issue.id.toString, 0, 100)

    issue.copy(changeLogs = changeLogs.right.get.values)
  }
}
