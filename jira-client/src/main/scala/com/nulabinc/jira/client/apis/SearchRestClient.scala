package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client.JiraRestClientError
import com.nulabinc.jira.client.domain.SearchResult

trait SearchRestClient {

  def searchJql(jql: String): Either[JiraRestClientError, SearchResult]

  def searchJql(jql: String, maxResults: Int = 50, startAt: Int = 0): Either[JiraRestClientError, SearchResult]
}
