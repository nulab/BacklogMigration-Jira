package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain._
import spray.json._

object IssueMappingJsonProtocol extends DefaultJsonProtocol {

  import UserMappingJsonProtocol._
  import IssueFieldMappingJsonProtocol._

  implicit object IssueMappingFormat extends RootJsonFormat[Issue] {
    def write(item: Issue) = JsObject(
      "id"     -> JsString(item.id.toString),
      "key"    -> JsString(item.key),
      "fields" -> JsObject(
        "description" -> item.description.toJson,
        "assignee"    -> item.assignee.toJson
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
            description = fieldMap.find(_._1 == "description").map(_._2.convertTo[String]),
            assignee    = fieldMap.find(_._1 == "assignee").map(_._2.convertTo[User]),
            issueFields = issueFields
          )
        }
        case other => deserializationError("Cannot deserialize Issue: invalid input. Raw input: " + other)
      }
    }
  }
}
