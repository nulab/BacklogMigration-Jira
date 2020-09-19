package com.nulabinc.jira.client.json

import java.util.Date

import com.nulabinc.jira.client.domain.{changeLog, _}
import com.nulabinc.jira.client.domain.changeLog._
import spray.json.{JsNull, _}

object ChangeLogMappingJsonProtocol extends DefaultJsonProtocol {

  import DateTimeMappingJsonProtocol._
  import UserMappingJsonProtocol._

  implicit object ChangeLogItemMappingFormat extends RootJsonFormat[ChangeLogItem] {
    def write(obj: ChangeLogItem) = ???

    def read(json: JsValue) = {
      val jsObject = json.asJsObject

      val from = jsObject.getFields("from") match {
        case Seq(JsString(s)) => Some(s)
        case _                => None
      }
      val fromString = jsObject.getFields("fromString") match {
        case Seq(JsString(s)) => Some(s)
        case _                => None
      }

      val to = jsObject.getFields("to") match {
        case Seq(JsNull)      => None
        case Seq(JsString(s)) => Some(s)
        case _                => None
      }
      val toString = jsObject.getFields("toString") match {
        case Seq(JsNull)      => None
        case Seq(JsString(s)) => Some(s)
        case _                => None
      }

      val fieldId = jsObject.getFields("fieldId") match {
        case Seq(JsString(id)) => Some(id)
        case _                 => None
      }

      jsObject.getFields("field", "fieldtype") match {
        case Seq(JsString(field), JsString(fieldType)) =>
          ChangeLogItem(
            field = ChangeLogItemField.parse(field),
            fieldType = fieldType,
            fieldId = fieldId.map(FieldId.parse),
            from = from,
            fromDisplayString = fromString,
            to = to,
            toDisplayString = toString
          )
        case other =>
          deserializationError(
            "Cannot deserialize ChangeLogItem: invalid input. Raw input: " + other
          )
      }
    }
  }

  implicit object ChangeLogMappingFormat extends RootJsonFormat[ChangeLog] {
    def write(obj: ChangeLog) = ???

    def read(json: JsValue): ChangeLog = {
      val jsObject = json.asJsObject
      val optAuthor = jsObject.getFields("author") match {
        case Seq(author) => Some(author.convertTo[User])
        case _           => None
      }

      jsObject.getFields("id", "created", "items") match {
        case Seq(JsString(id), created, items) =>
          changeLog.ChangeLog(
            id = id.toLong,
            optAuthor = optAuthor,
            createdAt = created.convertTo[Date],
            items = items.convertTo[Seq[ChangeLogItem]]
          )
        case other =>
          deserializationError(
            "Cannot deserialize ChangeLog: invalid input. Raw input: " + other
          )
      }
    }
  }

  implicit val changeLogResultMappingFormat: RootJsonFormat[ChangeLogResult] =
    jsonFormat3(ChangeLogResult)

}
