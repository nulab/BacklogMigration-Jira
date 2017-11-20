package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.domain.CommentResult
import spray.json._

class CommentAPI(httpClient: HttpClient) extends Pagenable {

  import com.nulabinc.jira.client.json.CommentMappingJsonProtocol._

  def issueComments(id: Long) = fetch(id.toString)

  def issueComments(projectKey: String) = fetch(projectKey)

  private def fetch(projectIdOrKey: String) =
    httpClient.get(s"/issue/$projectIdOrKey/comment") match {
      case Right(json)               => Right(JsonParser(json).convertTo[CommentResult].comments)
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("Component", projectIdOrKey))
      case Left(error)               => Left(HttpError(error))
    }
}
