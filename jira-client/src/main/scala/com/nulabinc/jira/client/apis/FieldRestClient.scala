package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client.JiraRestClientError
import com.nulabinc.jira.client.domain.Field

trait FieldRestClient {

  def all(): Either[JiraRestClientError, Seq[Field]]

}
