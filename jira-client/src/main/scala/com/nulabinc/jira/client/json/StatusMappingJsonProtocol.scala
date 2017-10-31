package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.Status
import spray.json.DefaultJsonProtocol

object StatusMappingJsonProtocol extends DefaultJsonProtocol {
  implicit val statusMappingFormat = jsonFormat2(Status)
}