package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.issue._
import org.joda.time._
import spray.json._

object IssueMappingJsonProtocol extends DefaultJsonProtocol {

  import UserMappingJsonProtocol._
  import IssueFieldMappingJsonProtocol._
  import TimeTrackMappingJsonProtocol._
  import IssueTypeMappingJsonProtocol._
  import ComponentMappingJsonProtocol._
  import StatusMappingJsonProtocol._
  import PriorityMappingJsonProtocol._
  import DateTimeMappingJsonProtocol._

  implicit object IssueMappingFormat extends RootJsonFormat[Issue] {
    def write(item: Issue) = ???

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
            components  = fieldMap.find(_._1 == "components").map(_._2.convertTo[Seq[Component]]).get,
            issueFields = issueFields,
            dueDate     = fieldMap.find(_._1 == "duedate").map(_._2.convertTo[DateTime]),
            timeTrack   = fieldMap.find(_._1 == "timetracking").map(_._2.convertTo[TimeTrack]).get,
            issueType   = fieldMap.find(_._1 == "issuetype").map(_._2.convertTo[IssueType]).get,
            status      = fieldMap.find(_._1 == "status").map(_._2.convertTo[Status]).get,
            priority    = fieldMap.find(_._1 == "priority").map(_._2.convertTo[Priority]).get,
            creator     = fieldMap.find(_._1 == "creator").map(_._2.convertTo[User]).get,
            createdAt   = fieldMap.find(_._1 == "created").map(_._2.convertTo[DateTime]).get,
            updatedAt   = fieldMap.find(_._1 == "updated").map(_._2.convertTo[DateTime]).get,
            changeLogs  = Seq.empty[ChangeLog]
          )
        }
        case other => deserializationError("Cannot deserialize Issue: invalid input. Raw input: " + other)
      }
    }
  }
}
