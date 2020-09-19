package com.nulabinc.backlog.j2b.formatters

import com.nulabinc.backlog.j2b.jira.domain.`export`.{ChangeLogMappingUser, ExistingMappingUser, MappingUser}
import spray.json._

object SprayJsonFormats extends DefaultJsonProtocol {

  implicit object MappingUserFormat extends RootJsonFormat[MappingUser] {
    def write(o: MappingUser): JsObject =
      o match {
        case m: ExistingMappingUser =>
          JsObject(
            "key"         -> JsString(m.key),
            "displayName" -> JsString(m.displayName),
            "optEmail"    -> m.optEmail.map(JsString(_)).getOrElse(JsNull)
          )
        case m: ChangeLogMappingUser =>
          JsObject(
            "key"         -> JsString(m.key),
            "displayName" -> JsString(m.displayName)
          )
      }

    def read(value: JsValue): MappingUser =
      value.asJsObject.getFields("key", "displayName", "optEmail") match {
        case Seq(JsString(key), JsString(displayName), JsString(optEmail)) =>
          ExistingMappingUser(
            key = key,
            displayName = displayName,
            Option(optEmail)
          )
        case Seq(JsString(key), JsString(displayName)) =>
          ChangeLogMappingUser(key = key, displayName = displayName)
        case other =>
          deserializationError(
            "Cannot deserialize MappingUser. Raw input: " + other
          )
      }

  }

}
