package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.User
import spray.json.DefaultJsonProtocol

object UserMappingJsonProtocol extends DefaultJsonProtocol {
  implicit val MappingFormat = jsonFormat2(User)
}
