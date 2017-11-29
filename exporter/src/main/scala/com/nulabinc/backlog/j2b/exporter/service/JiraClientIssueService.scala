package com.nulabinc.backlog.j2b.exporter.service

import java.util.Date
import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.j2b.jira.service.IssueService
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.jira.client.domain.changeLog.ChangeLog
import com.nulabinc.jira.client.{DownloadResult, JiraRestClient}
import com.nulabinc.jira.client.domain.issue.Issue

import scala.util.{Failure, Success, Try}
import scalax.file.Path

class JiraClientIssueService @Inject()(apiConfig: JiraApiConfiguration,
                                       projectKey: JiraProjectKey,
                                       jira: JiraRestClient,
                                       backlogPaths: BacklogPaths)
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

  // TODO: count
  override def changeLogs(issue: Issue): Seq[ChangeLog] =
    jira.issueAPI.changeLogs(issue.id.toString, 0, 100).right.get.values


  override def downloadAttachments(attachmentId: Long, saveDirectory: Path, fileName: String): DownloadResult = {
    // content = https://(workspace name).atlassian.net/secure/attachment/(attachment ID)/(file name)
    jira.httpClient.download(jira.url + s"/secure/attachment/$attachmentId/$fileName", saveDirectory.path)
  }

}
