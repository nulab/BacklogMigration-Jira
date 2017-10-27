package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client.JiraRestClientError
import com.nulabinc.jira.client.domain.Version

trait VersionRestClient extends Pagenable {

  def projectVersions(projectId: Long): Either[JiraRestClientError, Seq[Version]]

  def projectVersions(projectKey: String): Either[JiraRestClientError, Seq[Version]]

}
