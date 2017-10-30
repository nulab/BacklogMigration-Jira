package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client.JiraRestClientError
import com.nulabinc.jira.client.domain.Status

trait StatusRestClient {

  def statuses: Either[JiraRestClientError, Seq[Status]]
}
