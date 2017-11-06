package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.domain.Project
import spray.json.JsonParser

class ProjectAPI(httpClient: HttpClient) {

  import com.nulabinc.jira.client.json.ProjectMappingJsonProtocol._

  def project(id: Long) = fetchProject(id.toString)

  def project(key: String) = fetchProject(key)

  private [this] def fetchProject(projectIdOrKey: String) = {
    httpClient.get(s"/project/$projectIdOrKey") match {
      case Right(json)               => Right(JsonParser(json).convertTo[Project])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("Project", projectIdOrKey))
      case Left(error)               => Left(HttpError(error))
    }
  }
}
