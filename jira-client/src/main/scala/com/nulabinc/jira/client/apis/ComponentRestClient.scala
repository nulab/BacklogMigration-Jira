package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client.JiraRestClientError
import com.nulabinc.jira.client.domain.Component

trait ComponentRestClient extends Pagenable {

  def projectComponents(id: Long): Either[JiraRestClientError, Seq[Component]]

  def projectComponents(projectKey: String): Either[JiraRestClientError, Seq[Component]]

}
