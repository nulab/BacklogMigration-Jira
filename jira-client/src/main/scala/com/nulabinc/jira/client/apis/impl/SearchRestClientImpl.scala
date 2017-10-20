package com.nulabinc.jira.client.apis.impl

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.apis.SearchRestClient
import com.nulabinc.jira.client.domain.SearchResult
import spray.json.JsonParser
import com.netaporter.uri.dsl._

class SearchRestClientImpl(httpClient: HttpClient) extends SearchRestClient {

  import com.nulabinc.jira.client.json.SearchResultMappingJsonProtocol._

  override def searchJql(jql: String): Either[JiraRestClientError, SearchResult] = searchJql(jql, 50, 0)

  override def searchJql(jql: String, maxResults: Int, startAt: Int): Either[JiraRestClientError, SearchResult] = {
    val uri = "/search" ?
      ("startAt"    -> startAt) &
      ("maxResults" -> maxResults)

    httpClient.get(uri.toString + "&jql=" + jql) match {
      case Right(json)               => Right(JsonParser(json).convertTo[SearchResult])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("search", jql))
      case Left(error)               => Left(HttpError(error.toString))
    }
  }

}
