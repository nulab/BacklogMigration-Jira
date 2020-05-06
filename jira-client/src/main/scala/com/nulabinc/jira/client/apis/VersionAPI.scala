package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.domain.Version
import spray.json._

class VersionAPI(httpClient: HttpClient) {

  import com.nulabinc.jira.client.json.VersionMappingJsonProtocol._

  def projectVersions(projectId: Long) = fetch(projectId.toString)

  def projectVersions(projectKey: String) = fetch(projectKey)

  private def fetch(projectIdOrKey: String) =
    httpClient.get(s"/project/$projectIdOrKey/versions") match {
      case Right(json) => Right(JsonParser(json).convertTo[Seq[Version]])
      case Left(_: ApiNotFoundError) =>
        Left(ResourceNotFoundError("Version", projectIdOrKey))
      case Left(error) => Left(HttpError(error))
    }
}
