package com.nulabinc.jira.client.apis.impl

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.apis._
import spray.json._
import com.netaporter.uri.dsl._
import com.nulabinc.jira.client.domain.issue.Issue

case class IssueResult(total: Int, issues: Seq[Issue])

class IssueRestClientImpl(httpClient: HttpClient) extends IssueRestClient {

  import com.nulabinc.jira.client.json.IssueMappingJsonProtocol._
  import com.nulabinc.jira.client.json.IssueResultMappingJsonProtocol._

  override def issue(id: Long) = fetchIssue(id.toString)

  override def issue(key: String) = fetchIssue(key)

  override def projectIssues(key: String, startAt: Long = 0, maxResults: Long = 100) = {
    val uri = "/search" ?
      ("startAt"    -> startAt) &
      ("maxResults" -> maxResults) &
      ("jql"        -> s"project=$key")

    httpClient.get(uri.toString) match {
      case Right(json) => Right(JsonParser(json).convertTo[IssueResult].issues)
      case Left(error) => Left(HttpError(error.toString))
    }
  }

  private [this] def fetchIssue(issueIdOrKey: String) =
    httpClient.get(s"/issue/$issueIdOrKey") match {
      case Right(json)               => Right(JsonParser(json).convertTo[Issue])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("Issue", issueIdOrKey))
      case Left(error)               => Left(HttpError(error.toString))
    }
}
