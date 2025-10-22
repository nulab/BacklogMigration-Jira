package com.nulabinc.jira.client

import com.nulabinc.jira.client.apis._
import com.nulabinc.jira.client.domain.User
import spray.json._

class JiraRestClient(val url: String, username: String, apiKey: String) {

  val httpClient = new HttpClient(url, username, apiKey)

  lazy val projectAPI    = new ProjectAPI(httpClient)
  lazy val userAPI       = new UserAPI(httpClient)
  lazy val issueAPI      = new IssueRestClientImpl(httpClient)
  lazy val statusAPI     = new StatusAPI(httpClient)
  lazy val fieldAPI      = new FieldAPI(httpClient)
  lazy val componentAPI  = new ComponentAPI(httpClient)
  lazy val versionsAPI   = new VersionAPI(httpClient)
  lazy val issueTypeAPI  = new IssueTypeAPI(httpClient)
  lazy val priorityAPI   = new PriorityAPI(httpClient)
  lazy val attachmentAPI = new AttachmentAPI(httpClient)
  lazy val commentAPI    = new CommentAPI(httpClient)
  lazy val searchApproximateCountAPI = new SearchApproximateCountAPI(httpClient)

  def myself(): Either[JiraRestClientError, User] = {

    import json.UserMappingJsonProtocol._

    httpClient.get(s"/myself") match {
      case Right(json) => Right(JsonParser(json).convertTo[User])
      case Left(_: ApiNotFoundError) =>
        Left(ResourceNotFoundError("myself", s"$username:$apiKey"))
      case Left(error) => Left(HttpError(error))
    }
  }
}

object JiraRestClient {
  def apply(url: String, username: String, apiKey: String): JiraRestClient =
    new JiraRestClient(url, username, apiKey)
}
