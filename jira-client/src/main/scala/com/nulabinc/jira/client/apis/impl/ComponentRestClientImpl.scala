package com.nulabinc.jira.client.apis.impl

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.apis.ComponentRestClient
import spray.json._

class ComponentRestClientImpl(httpClient: HttpClient)  extends ComponentRestClient {

  import com.nulabinc.jira.client.json.ComponentMappingJsonProtocol._

  override def projectComponents(id: Long) = fetchProjectComponents(id.toString)

  override def projectComponents(projectKey: String) = fetchProjectComponents(projectKey)

  private def fetchProjectComponents(projectIdOrKey: String) =
    httpClient.get(s"/project/$projectIdOrKey/component") match {
      case Right(json)               => Right(JsonParser(json).convertTo[ComponentResult].values)
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("Component", projectIdOrKey))
      case Left(error)               => Left(HttpError(error.toString))
    }
}
