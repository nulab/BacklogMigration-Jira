package com.nulabinc.jira.client

import com.nulabinc.jira.client.apis._
import com.nulabinc.jira.client.domain.User
import spray.json._

class JiraRestClient(val url: String, username: String, password: String) {

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
  lazy val priorityAPI   = new PriorityAPI(httpClient)
  lazy val attachmentAPI = new AttachmentAPI(httpClient)

  def myself(): Either[JiraRestClientError, User] = {

    import json.UserMappingJsonProtocol._

    httpClient.get(s"/myself") match {
      case Right(json)               => Right(JsonParser(json).convertTo[User])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("myself", s"$username:$password"))
      case Left(error)               => Left(HttpError(error))
    }
  }
}

object JiraRestClient {
  def apply(url: String, username: String, password: String): JiraRestClient =
    new JiraRestClient(url, username, password)
}