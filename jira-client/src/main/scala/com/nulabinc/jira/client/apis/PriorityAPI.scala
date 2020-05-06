package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.domain.Priority
import spray.json.JsonParser

class PriorityAPI(httpClient: HttpClient) {

  import com.nulabinc.jira.client.json.PriorityMappingJsonProtocol._

  def priorities: Either[JiraRestClientError, Seq[Priority]] = {
    httpClient.get(s"/priority") match {
      case Right(json) => Right(JsonParser(json).convertTo[Seq[Priority]])
      case Left(_: ApiNotFoundError) =>
        Left(ResourceNotFoundError("priority", ""))
      case Left(error) => Left(HttpError(error))
    }
  }

}
