package com.nulabinc.jira.client

import com.nulabinc.jira.client.apis.impl._

class JiraRestClient(url: String, username: String, password: String) {

  val httpClient = new HttpClient(url, username, password)

  def projectRestClient = new ProjectRestClientImpl(httpClient)

  def userRestClient = new UserRestClientImpl(httpClient)

  def issueRestClient = new IssueRestClientImpl(httpClient)

  def searchRestClient = new SearchRestClientImpl(httpClient)

  def statusRestClient = new StatusRestClientImpl(httpClient)

  def fieldRestClient = new FieldRestClientImpl(httpClient)

  def componentRestClient = new ComponentRestClientImpl(httpClient)

  def versionsRestClient = new VersionRestClientImpl(httpClient)

  def issueTypeRestClient = new IssueTypeRestClientImpl(httpClient)
}

object JiraRestClient {
  def apply(url: String, username: String, password: String): JiraRestClient =
    new JiraRestClient(url, username, password)
}