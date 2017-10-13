package com.nulabinc.jira.client.apis.impl

import java.net.URLEncoder

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.apis.{UserMappingJsonProtocol, UserRestClient}
import com.nulabinc.jira.client.domain.User
import spray.json.JsonParser

class UserRestClientImpl(httpClient: HttpClient) extends UserRestClient {

  import UserMappingJsonProtocol._

  def users: Either[JiraRestClientError, Seq[User]] = chunkUsers(Seq.empty[User])

  def user(name: String): Either[JiraRestClientError, User] = {
    httpClient.get(s"/user?key=$name") match {
      case Right(user)               => Right(JsonParser(user).convertTo[User])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("user", name))
      case Left(error)               => Left(HttpError(error.toString))
    }
  }

  private [this] def chunkUsers(array: Seq[User], startAt: Int = 0): Either[JiraRestClientError, Seq[User]] = {
    val maxResults = 100
    val encodedUrl = URLEncoder.encode("%", "UTF-8")
    val url = s"/user/search?startAt=$startAt&maxResults=$maxResults&username=" + encodedUrl
    val body = httpClient.get(url)
    val result = body match {
      case Right(json) => Right(JsonParser(json).convertTo[Seq[User]])
      case Left(error)  => Left(HttpError(error.toString))
    }

    if (result.isLeft) result
    else if (result.right.get.isEmpty) Right(array)
    else chunkUsers(array ++ result.right.get, startAt + maxResults)
  }
}
