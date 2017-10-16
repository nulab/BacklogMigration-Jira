package com.nulabinc.jira.client

import com.nulabinc.jira.client.apis.impl._

class JiraRestClient(url: String, username: String, password: String) {

  val httpClient = new HttpClient(url, username, password)

  def userRestClient = new UserRestClientImpl(httpClient)

  def searchRestClient = new SearchRestClientImpl(httpClient)

  def statusRestClient = new StatusRestClientImpl(httpClient)
}

object JiraRestClient {
  def apply(url: String, username: String, password: String): JiraRestClient =
    new JiraRestClient(url, username, password)
}