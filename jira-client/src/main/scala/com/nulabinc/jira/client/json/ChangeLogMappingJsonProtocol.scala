package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain._
import org.joda.time.DateTime
import spray.json._

object ChangeLogMappingJsonProtocol extends DefaultJsonProtocol {

  import DateTimeMappingJsonProtocol._
  import UserMappingJsonProtocol._

  implicit object ChangeLogItemMappingFormat extends RootJsonFormat[ChangeLogItem] {
    def write(obj: ChangeLogItem) = ???

    def read(json: JsValue) = {
      val jsObject = json.asJsObject
      val from = jsObject.getFields("fromString") match {
        case Seq(JsString(from)) => Some(from)
        case _                   => None
      }
      jsObject.getFields("field", "fieldType", "fieldId", "toString") match {
        case Seq(JsString(field), JsString(fieldType), JsString(fieldId), JsString(to)) =>
          ChangeLogItem(
            field = field,
            fieldType = fieldType,
            fieldId = fieldId,
            from = from,
            to = to
          )
        case other => deserializationError("Cannot deserialize ChangeLogItem: invalid input. Raw input: " + other)
      }
    }
  }

  implicit object ChangeLogMappingFormat extends RootJsonFormat[ChangeLog] {
    def write(obj: ChangeLog) = ???

    def read(json: JsValue) = {
      val jsObject = json.asJsObject
      jsObject.getFields("id", "author", "created", "items") match {
        case Seq(JsString(id), author, created, items) =>
          ChangeLog(
            id        = id.toLong,
            author    = author.convertTo[User],
            createdAt = created.convertTo[DateTime],
            items     = items.convertTo[Seq[ChangeLogItem]]
          )
        case other => deserializationError("Cannot deserialize ChangeLog: invalid input. Raw input: " + other)
      }
    }
  }

  implicit val changeLogResultMappingFormat = jsonFormat3(ChangeLogResult)


}
