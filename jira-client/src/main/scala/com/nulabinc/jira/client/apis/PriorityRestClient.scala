package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client.JiraRestClientError
import com.nulabinc.jira.client.domain.Priority
import spray.json.DefaultJsonProtocol

object PriorityMappingJsonProtocol extends DefaultJsonProtocol {
  implicit val MappingFormat = jsonFormat1(Priority)
}

trait PriorityRestClient {

  def priorities: Either[JiraRestClientError, Seq[Priority]]

}
