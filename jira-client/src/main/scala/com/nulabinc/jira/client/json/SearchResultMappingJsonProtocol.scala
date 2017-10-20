package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.SearchResult
import spray.json.DefaultJsonProtocol

object SearchResultMappingJsonProtocol extends DefaultJsonProtocol {
  import IssueMappingJsonProtocol._

  implicit val MappingFormat = jsonFormat4(SearchResult)
}
