package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.{Issue, IssueField}
import spray.json._

object IssueMappingJsonProtocol extends DefaultJsonProtocol {

  import UserMappingJsonProtocol._

  implicit val IssueFieldMappingFormat  = jsonFormat2(IssueField)
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
          val issueFields = IssueFieldMappingFormat.read(fields)
          Issue(
            id          = id.toLong,
            key         = key,
            description = issueFields.description,
            assignee    = issueFields.assignee
          )
        }
        case other => deserializationError("Cannot deserialize Issue: invalid input. Raw input: " + other)
      }
    }
  }
}
