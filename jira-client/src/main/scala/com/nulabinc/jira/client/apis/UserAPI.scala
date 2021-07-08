package com.nulabinc.jira.client.apis

import java.net.URLEncoder

import io.lemonlabs.uri.dsl._
import com.nulabinc.jira.client._
import com.nulabinc.jira.client.domain.User
import spray.json.JsonParser

class UserAPI(httpClient: HttpClient) {

  import com.nulabinc.jira.client.json.UserMappingJsonProtocol._

  def users: Either[JiraRestClientError, Seq[User]] =
    chunkUsers(Seq.empty[User])

  def findByUsername(name: String): Either[JiraRestClientError, User] =
    httpClient.get(s"/user?username=${encode(name)}") match {
      case Right(json) => Right(JsonParser(json).convertTo[User])
      case Left(_: ApiNotFoundError) =>
        Left(ResourceNotFoundError("user", name))
      case Left(error) => Left(HttpError(error))
    }

  def findByKey(key: String): Either[JiraRestClientError, User] =
    httpClient.get(s"/user?key=${encode(key)}") match {
      case Right(json)               => Right(JsonParser(json).convertTo[User])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("user", key))
      case Left(error)               => Left(HttpError(error))
    }

  private[this] def chunkUsers(
      beforeUsers: Seq[User],
      startAt: Int = 0
  ): Either[JiraRestClientError, Seq[User]] = {
    val maxResults = 100
    val uri = "/user/search" ?
      ("startAt" -> startAt) &
      ("maxResults" -> maxResults) &
      ("username" -> "%")
    val body = httpClient.get(uri.toString)
    val result = body match {
      case Right(json) => Right(JsonParser(json).convertTo[Seq[User]])
      case Left(error) => Left(HttpError(error))
    }

    if (result.isLeft) result
    else if (result.right.get.isEmpty) Right(beforeUsers)
    else chunkUsers(beforeUsers ++ result.right.get, startAt + maxResults)
  }

  private[this] def encode(string: String): String =
    URLEncoder.encode(string, "UTF-8")
}
