package com.nulabinc.jira.client.apis.impl

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.apis.{StatusRestClient, StatusMappingJsonProtocol}
import com.nulabinc.jira.client.domain.Status
import spray.json.JsonParser

class StatusRestClientImpl(httpClient: HttpClient) extends StatusRestClient {

  import StatusMappingJsonProtocol._

  override def statuses: Either[JiraRestClientError, Seq[Status]] = {
    httpClient.get(s"/status") match {
      case Right(json)               => Right(JsonParser(json).convertTo[Seq[Status]])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("status", ""))
      case Left(error)               => Left(HttpError(error.toString))
    }
  }
}