package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client._
import spray.json._

class ComponentAPI(httpClient: HttpClient) {

  import com.nulabinc.jira.client.json.ComponentMappingJsonProtocol._

  def projectComponents(id: Long) = fetchProjectComponents(id.toString)

  def projectComponents(projectKey: String) = fetchProjectComponents(projectKey)

  private def fetchProjectComponents(projectIdOrKey: String) =
    httpClient.get(s"/project/$projectIdOrKey/component") match {
      case Right(json)               => Right(JsonParser(json).convertTo[ComponentResult].values)
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("Component", projectIdOrKey))
      case Left(error)               => Left(HttpError(error))
    }
}
