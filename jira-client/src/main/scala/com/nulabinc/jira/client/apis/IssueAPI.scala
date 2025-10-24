package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.domain.changeLog.{ChangeLog, ChangeLogResult}
import com.nulabinc.jira.client.domain.issue.Issue
import io.lemonlabs.uri.typesafe.dsl._
import spray.json._

case class IssueResult(issues: Seq[Issue], isLast: Boolean, nextPageToken: Option[String])

class IssueRestClientImpl(httpClient: HttpClient) extends Pageable {

  import com.nulabinc.jira.client.json.IssueMappingJsonProtocol._
  import com.nulabinc.jira.client.json.IssueResultMappingJsonProtocol._
  import com.nulabinc.jira.client.json.ChangeLogMappingJsonProtocol._

  def issue(id: Long) = fetchIssue(id.toString)

  def issue(key: String) = fetchIssue(key)

  def projectIssues(
      key: String,
      nextPageToken: Option[String],
      startAt: Long = 0,
      maxResults: Long = 100,
  ): Either[HttpError, IssueResult] = {
    val params = Seq(
      Some("jql"    -> s"project=$key"),
      Some("fields" -> "*all"),
      nextPageToken.map(token => "nextPageToken" -> token)
    ).flatten

    val uri = params.foldLeft(
      "/search/jql" ? paginateUri(startAt, maxResults)
    ) { case (uri, param) => uri & param }

    httpClient.get(uri.toString) match {
      case Right(json) => Right(JsonParser(json).convertTo[IssueResult])
      case Left(error) => Left(HttpError(error))
    }
  }

  def changeLogs(
      issueIdOrKey: String,
      startAt: Long,
      maxResults: Long
  ): Either[JiraRestClientError, ChangeLogResult] = {
    val uri = s"/issue/$issueIdOrKey/changelog" ? paginateUri(startAt, maxResults)
    httpClient.get(uri.toString) match {
      case Right(json) => Right(JsonParser(json).convertTo[ChangeLogResult])
      case Left(_: ApiNotFoundError) =>
        Right(ChangeLogResult(0, true, Seq.empty[ChangeLog]))
      case Left(error) => Left(HttpError(error))
    }
  }

  private[this] def fetchIssue(issueIdOrKey: String) =
    httpClient.get(s"/issue/$issueIdOrKey") match {
      case Right(json) => Right(JsonParser(json).convertTo[Issue])
      case Left(_: ApiNotFoundError) =>
        Left(ResourceNotFoundError("Issue", issueIdOrKey))
      case Left(error) => Left(HttpError(error))
    }
}
