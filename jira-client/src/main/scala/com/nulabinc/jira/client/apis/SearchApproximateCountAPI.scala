package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.domain.ApproximateCount
import com.nulabinc.jira.client.json.ApproximateCountMappingJsonProtocol._
import spray.json._

class SearchApproximateCountAPI(httpClient: HttpClient) {

  def count(jql: String): Either[JiraRestClientError, ApproximateCount] = {
    val uri = "/search/approximate-count"
    val requestBody = JsObject("jql" -> JsString(jql)).toString()

    httpClient.post(uri, requestBody) match {
      case Right(json) =>
        Right(JsonParser(json).convertTo[ApproximateCount])
      case Left(_: ApiNotFoundError) =>
        Left(ResourceNotFoundError("search/approximate-count", jql))
      case Left(error) => Left(HttpError(error))
    }
  }
}
