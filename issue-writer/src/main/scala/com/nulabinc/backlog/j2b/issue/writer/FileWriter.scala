package com.nulabinc.backlog.j2b.issue.writer

import java.io.PrintWriter
import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.jira.client.domain.Issue
import com.nulabinc.jira.client.{JiraRestClient, JiraRestClientError}
import spray.json._

class FileWriter @Inject()(jiraClient: JiraRestClient) extends IssueWriteService
    with Logging {

  import com.nulabinc.jira.client.apis.IssueMappingJsonProtocol._

  override def write(projectKey: JiraProjectKey, filePath: String): Either[IssueIOError, Seq[Issue]] = {
    fetch(projectKey.value, Seq.empty[Issue], 0, 100) match {
      case Right(issues) => {
        val pw = new PrintWriter(filePath)
        try {
          issues.foreach { issue =>
            pw.write(issue.toJson + "\n")
          }
          Right(issues)
        } catch {
          case e: Throwable =>
            logger.error(e.getMessage, e)
            Left(IssueWritingError)
        } finally {
          pw.close
        }
      }
      case Left(error) => {
        logger.error(error.message)
        Left(IssueFetchingError(error.message))
      }
    }
  }

  private [writer] def fetch(projectKey: String,
                             beforeIssues: Seq[Issue],
                             startAt: Long,
                             maxResults: Long): Either[JiraRestClientError, Seq[Issue]] = {

    val result = jiraClient.issueRestClient.projectIssues(projectKey, startAt, maxResults)

    if (result.isLeft) result
    else if (result.right.get.isEmpty) Right(beforeIssues)
    else fetch(projectKey, beforeIssues ++ result.right.get, startAt + maxResults, maxResults)
  }
}
