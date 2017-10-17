package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client.JiraRestClientError
import com.nulabinc.jira.client.domain.User
import spray.json.DefaultJsonProtocol

object UserMappingJsonProtocol extends DefaultJsonProtocol {
  implicit val MappingFormat = jsonFormat2(User)
}

trait UserRestClient {

  def users: Either[JiraRestClientError, Seq[User]]

  def user(name: String): Either[JiraRestClientError, User]

}
