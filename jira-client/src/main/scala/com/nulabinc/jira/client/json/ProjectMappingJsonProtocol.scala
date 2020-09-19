package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.Project
import spray.json._

object ProjectMappingJsonProtocol extends DefaultJsonProtocol {
  implicit object IdFormat extends RootJsonFormat[Project] {
    def write(c: Project) =
      JsObject(
        "id"          -> JsString(c.id.toString),
        "key"         -> JsString(c.key),
        "name"        -> JsString(c.name),
        "description" -> JsString(c.description)
      )
    def read(value: JsValue) = {
      value.asJsObject.getFields("id", "key", "name", "description") match {
        case Seq(
              JsString(id),
              JsString(key),
              JsString(name),
              JsString(description)
            ) =>
          Project(id.toLong, key, name, description)
        case _ => throw new DeserializationException("Project expected")
      }
    }
  }
}
