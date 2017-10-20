package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client.JiraRestClientError
import com.nulabinc.jira.client.domain.Project

trait ProjectRestClient {

  def project(id: Long): Either[JiraRestClientError, Project]

  def project(key: String): Either[JiraRestClientError, Project]
}
