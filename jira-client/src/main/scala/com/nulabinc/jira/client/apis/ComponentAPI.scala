package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.domain.Component
import spray.json._

class ComponentAPI(httpClient: HttpClient) {

  import com.nulabinc.jira.client.json.ComponentMappingJsonProtocol._

  def projectComponents(id: Long) = fetchProjectComponents(id.toString)

  def projectComponents(projectKey: String) = fetchProjectComponents(projectKey)

  private def fetchProjectComponents(projectIdOrKey: String) =
    httpClient.get(s"/project/$projectIdOrKey/components") match {
      case Right(json)               => Right(JsonParser(json).convertTo[Seq[Component]])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("Component", projectIdOrKey))
      case Left(error)               => Left(HttpError(error))
    }
}
