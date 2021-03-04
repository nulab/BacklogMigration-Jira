package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.User
import spray.json._

import scala.util.Try

object UserMappingJsonProtocol extends DefaultJsonProtocol {

  // Supports JIRA server
  implicit object UserFormats extends RootJsonFormat[User] {
    private val userMappingFormat = jsonFormat3(User)

    def read(json: JsValue) =
      Try(userMappingFormat.read(json)).getOrElse {
        val obj = json.asJsObject
        obj.getFields("key", "name", "emailAddress") match {
          case Seq(JsString(key), JsString(name), JsString(email)) =>
            User(
              accountId = key,
              displayName = name,
              emailAddress = Some(email)
            )
          case Seq(JsString(key), JsString(name)) =>
            User(accountId = key, displayName = name, emailAddress = None)
          case others =>
            throw deserializationError("Cannot deserialize User. Input: " + others)
        }
      }
    override def write(obj: User): JsValue = ???
  }

}
