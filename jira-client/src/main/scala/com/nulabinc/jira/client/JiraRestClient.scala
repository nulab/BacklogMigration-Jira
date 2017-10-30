package com.nulabinc.jira.client

import com.nulabinc.jira.client.apis._

class JiraRestClient(url: String, username: String, password: String) {

  val httpClient = new HttpClient(url, username, password)

  lazy val projectAPI    = new ProjectAPI(httpClient)
  lazy val userAPI       = new UserAPI(httpClient)
  lazy val issueAPI      = new IssueRestClientImpl(httpClient)
  lazy val searchAPI     = new SearchAPI(httpClient)
  lazy val statusAPI     = new StatusAPI(httpClient)
  lazy val fieldAPI      = new FieldAPI(httpClient)
  lazy val componentAPI  = new ComponentAPI(httpClient)
  lazy val versionsAPI   = new VersionAPI(httpClient)
  lazy val issueTypeAPI  = new IssueTypeAPI(httpClient)
  lazy val changeLogAPI  = new ChangeLogAPI(httpClient)
}

object JiraRestClient {
  def apply(url: String, username: String, password: String): JiraRestClient =
    new JiraRestClient(url, username, password)
}