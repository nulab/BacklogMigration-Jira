package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain._
import org.joda.time._
import org.joda.time.format.ISODateTimeFormat
import spray.json._

object IssueMappingJsonProtocol extends DefaultJsonProtocol {

  import UserMappingJsonProtocol._
  import IssueFieldMappingJsonProtocol._

  implicit object DateTimeJsonFormat extends RootJsonFormat[DateTime] {
    private lazy val format = ISODateTimeFormat.dateTimeNoMillis()
    def write(datetime: DateTime): JsValue = JsString(format.print(datetime.withZone(DateTimeZone.UTC)))
    def read(json: JsValue): DateTime = json match {
      case JsString(x) => format.parseDateTime(x)
      case x           => deserializationError("Expected DateTime as JsString, but got " + x)
    }
  }

  implicit object IssueMappingFormat extends RootJsonFormat[Issue] {
    def write(item: Issue) = JsObject(
      "id"     -> JsString(item.id.toString),
      "key"    -> JsString(item.key),
      "fields" -> JsObject(
        "summary"     -> item.summary.toJson,
        "description" -> item.description.toJson,
        "parent"      -> item.parent.toJson,
        "assignee"    -> item.assignee.toJson,
        "duedate"     -> item.dueDate.toJson
      )
    )

    def read(value: JsValue) = {
      val jsObject = value.asJsObject

      jsObject.getFields("id", "key", "fields") match {
        case Seq(JsString(id), JsString(key), fields) => {
          val fieldMap = fields.convertTo[Map[String, JsValue]]
          val issueFields = fieldMap
            .filter(_._1.startsWith("customfield_"))
            .filterNot { map => map._2 == JsNull }
            .map { item =>
              IssueField(
                id    = item._1,
                value = item._2.convertTo[FieldValue]
              )
            }
            .toSeq

          Issue(
            id          = id.toLong,
            key         = key,
            summary     = fieldMap.find(_._1 == "summary").map(_._2.convertTo[String]).getOrElse(""),
            description = fieldMap.find(_._1 == "description").map(_._2.convertTo[String]),
            parent      = fieldMap.find(_._1 == "parent").map(_._2.convertTo[Issue]),
            assignee    = fieldMap.find(_._1 == "assignee").map(_._2.convertTo[User]),
            issueFields = issueFields,
            dueDate     = fieldMap.find(_._1 == "duedate").map(_._2.convertTo[DateTime])
          )
        }
        case other => deserializationError("Cannot deserialize Issue: invalid input. Raw input: " + other)
      }
    }
  }
}
