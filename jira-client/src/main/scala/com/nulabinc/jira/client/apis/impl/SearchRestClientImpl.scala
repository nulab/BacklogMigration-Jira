package com.nulabinc.jira.client.apis.impl

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.apis.{SearchRestClient, SearchResultMappingJsonProtocol}
import com.nulabinc.jira.client.domain.SearchResult
import spray.json.JsonParser
import com.netaporter.uri.dsl._
import com.netaporter.uri.Uri
import com.netaporter.uri.config.UriConfig
import com.netaporter.uri.decoding.NoopDecoder

class SearchRestClientImpl(httpClient: HttpClient) extends SearchRestClient {

  import SearchResultMappingJsonProtocol._

  implicit val config = UriConfig(decoder = NoopDecoder)

  override def searchJql(jql: String): Either[JiraRestClientError, SearchResult] = searchJql(jql, 50, 0)

  override def searchJql(jql: String, maxResults: Int, startAt: Int): Either[JiraRestClientError, SearchResult] = {
    val uri = Uri.parse("/search?" + jql) &
      ("startAt"    -> startAt) &
      ("maxResults" -> maxResults)

    httpClient.get(uri.toString) match {
      case Right(json)               => Right(JsonParser(json).convertTo[SearchResult])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("search", jql))
      case Left(error)               => Left(HttpError(error.toString))
    }
  }

}
