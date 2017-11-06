package com.nulabinc.jira.client.apis

import com.netaporter.uri.dsl._
import com.nulabinc.jira.client._
import com.nulabinc.jira.client.domain.SearchResult
import spray.json.JsonParser

class SearchAPI(httpClient: HttpClient) {

  import com.nulabinc.jira.client.json.SearchResultMappingJsonProtocol._

  def searchJql(jql: String): Either[JiraRestClientError, SearchResult] = searchJql(jql, 50, 0)

  def searchJql(jql: String, maxResults: Int, startAt: Int): Either[JiraRestClientError, SearchResult] = {
    val uri = "/search" ?
      ("startAt"    -> startAt) &
      ("maxResults" -> maxResults)

    httpClient.get(uri.toString + "&jql=" + jql) match {
      case Right(json)               => Right(JsonParser(json).convertTo[SearchResult])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("search", jql))
      case Left(error)               => Left(HttpError(error))
    }
  }

}
