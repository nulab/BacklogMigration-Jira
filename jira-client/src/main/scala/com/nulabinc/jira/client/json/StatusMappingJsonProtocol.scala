package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.Status
import spray.json.DefaultJsonProtocol

object StatusMappingJsonProtocol extends DefaultJsonProtocol {
  case class StatusWrapper(statuses: Seq[Status])

  implicit val statusMappingFormat = jsonFormat2(Status)
  implicit val statusWrapperMappingFormat = jsonFormat1(StatusWrapper)
}
