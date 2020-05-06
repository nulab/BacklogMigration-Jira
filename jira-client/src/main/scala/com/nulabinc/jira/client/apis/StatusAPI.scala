package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.domain.Status
import spray.json.JsonParser

class StatusAPI(httpClient: HttpClient) {

  import com.nulabinc.jira.client.json.StatusMappingJsonProtocol._

  def statuses(
      projectIdOrKey: String
  ): Either[JiraRestClientError, Seq[Status]] = {
    httpClient.get(s"/project/$projectIdOrKey/statuses") match {
      case Right(json) =>
        val statuses = JsonParser(json)
          .convertTo[Seq[StatusWrapper]]
          .foldLeft(Set.empty[Status]) { (acc, item) =>
            acc ++ item.statuses.toSet
          }
          .toSeq
        Right(statuses)
      case Left(_: ApiNotFoundError) =>
        Left(ResourceNotFoundError("status", ""))
      case Left(error) =>
        Left(HttpError(error))
    }
  }
}
