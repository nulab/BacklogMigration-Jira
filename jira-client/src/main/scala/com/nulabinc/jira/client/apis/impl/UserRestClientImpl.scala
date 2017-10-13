package com.nulabinc.jira.client.apis.impl

import java.net.URLEncoder

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.apis.{UserMappingJsonProtocol, UserRestClient}
import com.nulabinc.jira.client.domain.User
import spray.json.JsonParser

class UserRestClientImpl(httpClient: HttpClient) extends UserRestClient {

  import UserMappingJsonProtocol._

  def users: Either[JiraRestClientError, Seq[User]] = {
    val encodedUrl = URLEncoder.encode("%", "UTF-8")
    val url = "/user/search?startAt=0&maxResults=1000&username=" + encodedUrl
    val body = httpClient.get(url)
    body match {
      case Right(users) => Right(JsonParser(users).convertTo[Seq[User]])
      case Left(error)  => Left(HttpError(error.toString))
    }

  }

  def user(name: String): Either[JiraRestClientError, User] = {
    httpClient.get(s"/user?key=$name") match {
      case Right(user)               => Right(JsonParser(user).convertTo[User])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("user", name))
      case Left(error)               => Left(HttpError(error.toString))
    }
  }
}
