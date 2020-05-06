package com.nulabinc.jira.client.apis

import io.lemonlabs.uri.dsl._
import com.nulabinc.jira.client._
import com.nulabinc.jira.client.domain.CommentResult
import spray.json._

class CommentAPI(httpClient: HttpClient) extends Pageable {

  import com.nulabinc.jira.client.json.CommentMappingJsonProtocol._

  def issueComments(
      id: Long,
      startAt: Long,
      maxResults: Long
  ): Either[JiraRestClientError, CommentResult] =
    fetch(id.toString, startAt, maxResults)

  def issueComments(
      projectKey: String,
      startAt: Long,
      maxResults: Long
  ): Either[JiraRestClientError, CommentResult] =
    fetch(projectKey, startAt, maxResults)

  private def fetch(
      projectIdOrKey: String,
      startAt: Long,
      maxResults: Long
  ): Either[JiraRestClientError, CommentResult] = {
    val uri =
      s"/issue/$projectIdOrKey/comment" ? paginateUri(startAt, maxResults)
    httpClient.get(uri.toString) match {
      case Right(json) => Right(JsonParser(json).convertTo[CommentResult])
      case Left(_: ApiNotFoundError) =>
        Left(ResourceNotFoundError("Component", projectIdOrKey))
      case Left(error) => Left(HttpError(error))
    }
  }
}
