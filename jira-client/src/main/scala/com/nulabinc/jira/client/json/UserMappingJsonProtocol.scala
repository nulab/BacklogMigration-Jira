package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.User
import spray.json._

object UserMappingJsonProtocol extends DefaultJsonProtocol {
  implicit val userMappingFormat = jsonFormat4(User)
}
