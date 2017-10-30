package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client.JiraRestClientError
import com.nulabinc.jira.client.domain.Priority

trait PriorityRestClient {

  def priorities: Either[JiraRestClientError, Seq[Priority]]

}
