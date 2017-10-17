package com.nulabinc.jira.client.apis.impl

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.apis._
import com.nulabinc.jira.client.domain.Issue
import spray.json._
import com.netaporter.uri.dsl._

case class IssueResult(total: Int, issues: Seq[Issue])

object IssueResultMappingJsonProtocol extends DefaultJsonProtocol {
  import IssueMappingJsonProtocol._
  implicit val IssueResultFieldMappingFormat = jsonFormat2(IssueResult)
}

class IssueRestClientImpl(httpClient: HttpClient) extends IssueRestClient {

  import IssueMappingJsonProtocol._
  import IssueResultMappingJsonProtocol._

  override def issue(id: Long) = fetchIssue(id.toString)

  override def issue(key: String) = fetchIssue(key)

  override def projectIssues(key: String) = chunkIssues(s"project=$key", Seq.empty[Issue], 0)

  private [this] def fetchIssue(issueIdOrKey: String) =
    httpClient.get(s"/issue/$issueIdOrKey") match {
      case Right(json)               => Right(JsonParser(json).convertTo[Issue])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("Issue", issueIdOrKey))
      case Left(error)               => Left(HttpError(error.toString))
    }

  private [this] def chunkIssues(jql: String, beforeIssues: Seq[Issue], startAt: Int = 0): Either[JiraRestClientError, Seq[Issue]] = {
    val maxResults = 100
    val uri = "/search" ?
      ("startAt"    -> startAt) &
      ("maxResults" -> maxResults) &
      ("jql"        -> jql)
    val body = httpClient.get(uri.toString)
    val result = body match {
      case Right(json) => Right(JsonParser(json).convertTo[IssueResult].issues)
      case Left(error) => Left(HttpError(error.toString))
    }

    if (result.isLeft) result
    else if (result.right.get.isEmpty) Right(beforeIssues)
    else chunkIssues(jql, beforeIssues ++ result.right.get, startAt + maxResults)
  }
}
