package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.ApproximateCount
import spray.json.DefaultJsonProtocol

object ApproximateCountMappingJsonProtocol extends DefaultJsonProtocol {
  implicit val approximateCountFormat = jsonFormat1(ApproximateCount)
}
