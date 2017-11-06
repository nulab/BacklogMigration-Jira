package com.nulabinc.jira.client

sealed abstract class JiraRestClientError(val message: String) {
  override def toString: String = message
}

case class HttpError(clientError: HttpClientError) extends JiraRestClientError(clientError.message)
case class ResourceNotFoundError(resourceName: String, key: String)
  extends JiraRestClientError(s"Resource: [$resourceName] not found. key = $key")