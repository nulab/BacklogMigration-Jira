package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client.JiraRestClientError
import com.nulabinc.jira.client.domain.Status
import spray.json.DefaultJsonProtocol

object StatusMappingJsonProtocol extends DefaultJsonProtocol {
  implicit val MappingFormat = jsonFormat2(Status)
}

trait StatusRestClient {

  def statuses: Either[JiraRestClientError, Seq[Status]]
}
