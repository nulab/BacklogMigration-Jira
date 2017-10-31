package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.Priority
import spray.json.DefaultJsonProtocol

object PriorityMappingJsonProtocol extends DefaultJsonProtocol {
  implicit val priorityMappingFormat = jsonFormat1(Priority)
}
