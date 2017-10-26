package com.nulabinc.jira.client.apis.impl

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.apis.VersionRestClient
import spray.json._

class VersionRestClientImpl(httpClient: HttpClient) extends VersionRestClient {

  import com.nulabinc.jira.client.json.VersionMappingJsonProtocol._

  override def projectVersions(projectId: Long) = fetch(projectId.toString)

  override def projectVersions(projectKey: String) = fetch(projectKey)

  private def fetch(projectIdOrKey: String) =
    httpClient.get(s"/project/$projectIdOrKey/version") match {
      case Right(json)               => Right(JsonParser(json).convertTo[VersionResult].values)
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("Version", projectIdOrKey))
      case Left(error)               => Left(HttpError(error.toString))
    }
}
