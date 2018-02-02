package com.nulabinc.jira.client.apis

import com.netaporter.uri.dsl._
import com.nulabinc.jira.client._
import com.nulabinc.jira.client.domain.changeLog.{ChangeLog, ChangeLogResult}
import com.nulabinc.jira.client.domain.issue.Issue
import spray.json._

case class IssueResult(total: Int, issues: Seq[Issue])

class IssueRestClientImpl(httpClient: HttpClient) extends Pageable {

  import com.nulabinc.jira.client.json.IssueMappingJsonProtocol._
  import com.nulabinc.jira.client.json.IssueResultMappingJsonProtocol._
  import com.nulabinc.jira.client.json.ChangeLogMappingJsonProtocol._

  def issue(id: Long) = fetchIssue(id.toString)

  def issue(key: String) = fetchIssue(key)

  def projectIssues(key: String, startAt: Long = 0, maxResults: Long = 100): Either[HttpError, Seq[Issue]] = {
    val uri = "/search" ? paginateUri(startAt, maxResults) &
      ("jql"        -> s"project=$key") &
      ("fields"     -> "*all")

    httpClient.get(uri.toString) match {
      case Right(json) => Right(JsonParser(json).convertTo[IssueResult].issues)
      case Left(error) => Left(HttpError(error))
    }
  }

  def changeLogs(issueIdOrKey: String, startAt: Long, maxResults: Long): Either[JiraRestClientError, ChangeLogResult] =
    httpClient.get(s"/issue/$issueIdOrKey/changelog") match {
      case Right(json)               => Right(JsonParser(json).convertTo[ChangeLogResult])
      case Left(_: ApiNotFoundError) => Right(ChangeLogResult(0, true, Seq.empty[ChangeLog]))
      case Left(error)               => Left(HttpError(error))
    }

  private [this] def fetchIssue(issueIdOrKey: String) =
    httpClient.get(s"/issue/$issueIdOrKey") match {
      case Right(json)               => Right(JsonParser(json).convertTo[Issue])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("Issue", issueIdOrKey))
      case Left(error)               => Left(HttpError(error))
    }
}
