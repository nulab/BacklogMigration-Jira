package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.issue.IssueType
import spray.json._

object IssueTypeMappingJsonProtocol extends DefaultJsonProtocol {

  implicit object IssueTypeMappingFormat extends RootJsonFormat[IssueType] {
    def write(c: IssueType) = ???

    def read(value: JsValue) = {
      value.asJsObject.getFields("id", "name", "subtask", "description") match {
        case Seq(JsString(id), JsString(name), JsBoolean(isSubTask), JsString(description)) =>
          IssueType(
            id = id.toLong,
            name = name,
            isSubTask = isSubTask,
            description = description
          )
        case other => deserializationError("Cannot deserialize Component: invalid input. Raw input: " + other)
      }
    }
  }
}
