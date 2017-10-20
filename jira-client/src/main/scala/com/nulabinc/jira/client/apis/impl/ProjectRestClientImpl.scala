package com.nulabinc.jira.client.apis.impl

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.apis._
import com.nulabinc.jira.client.domain.Project
import spray.json.JsonParser

class ProjectRestClientImpl(httpClient: HttpClient) extends ProjectRestClient {

  import com.nulabinc.jira.client.json.ProjectMappingJsonProtocol._

  override def project(id: Long) = fetchProject(id.toString)

  override def project(key: String) = fetchProject(key)

  private [this] def fetchProject(projectIdOrKey: String) = {
    httpClient.get(s"/project/$projectIdOrKey") match {
      case Right(json)               => Right(JsonParser(json).convertTo[Project])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("Project", projectIdOrKey))
      case Left(error)               => Left(HttpError(error.toString))
    }
  }
}
