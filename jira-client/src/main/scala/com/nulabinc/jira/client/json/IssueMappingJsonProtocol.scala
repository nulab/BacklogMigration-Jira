package com.nulabinc.jira.client.json

import java.text.SimpleDateFormat
import java.util.Date

import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.changeLog.ChangeLog
import com.nulabinc.jira.client.domain.issue._
import org.joda.time._
import spray.json._

import scala.util.Try

object IssueMappingJsonProtocol extends DefaultJsonProtocol {

  import UserMappingJsonProtocol._
  import IssueFieldMappingJsonProtocol._
  import TimeTrackMappingJsonProtocol._
  import IssueTypeMappingJsonProtocol._
  import ComponentMappingJsonProtocol._
  import StatusMappingJsonProtocol._
  import PriorityMappingJsonProtocol._
  import DateTimeMappingJsonProtocol._
  import VersionMappingJsonProtocol._
  import AttachmentMappingJsonProtocol._

  implicit object DateFormat extends JsonFormat[Date] {
    def write(date: Date) = ???
    def read(json: JsValue) = json match {
      case JsString(rawDate) =>
        parseIsoDateString(rawDate)
          .fold(deserializationError(s"Expected ISO Date format, got $rawDate"))(identity)
      case error => deserializationError(s"Expected JsString, got $error")
    }

    private val localIsoDateFormatter = new ThreadLocal[SimpleDateFormat] {
      override def initialValue() = new SimpleDateFormat("yyyy-MM-dd")
    }

    private def parseIsoDateString(date: String): Option[Date] =
      Try{ localIsoDateFormatter.get().parse(date) }.toOption
  }

  implicit object ParentIssueMappingFormat extends RootJsonFormat[ParentIssue] {
    def write(obj: ParentIssue): JsValue = ???

    def read(json: JsValue): ParentIssue =
      json.asJsObject.getFields("id") match {
        case Seq(JsString(id)) => ParentIssue(id.toLong)
        case other => deserializationError("Cannot deserialize ParentIssue: invalid input. Raw input: " + other)
      }
  }

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
            description = fieldMap.find(_._1 == "description").filterNot(_._2 == JsNull).map(_._2.convertTo[String]),
            parent      = fieldMap.find(_._1 == "parent").map(_._2.convertTo[ParentIssue]),
            assignee    = fieldMap.find(_._1 == "assignee").filterNot(_._2 == JsNull).map(_._2.convertTo[User]),
            components  = fieldMap.find(_._1 == "components").map(_._2.convertTo[Seq[Component]]).getOrElse(Seq.empty[Component]),
            fixVersions = fieldMap.find(_._1 == "fixVersions").map(_._2.convertTo[Seq[Version]]).getOrElse(Seq.empty[Version]),
            issueFields = issueFields,
            dueDate     = fieldMap.find(_._1 == "duedate").filterNot(_._2 == JsNull).map(_._2.convertTo[Date]),
            timeTrack   = fieldMap.find(_._1 == "timetracking").map(_._2.convertTo[TimeTrack]),
            issueType   = fieldMap.find(_._1 == "issuetype").map(_._2.convertTo[IssueType]).get,
            status      = fieldMap.find(_._1 == "status").map(_._2.convertTo[Status]).get,
            priority    = fieldMap.find(_._1 == "priority").map(_._2.convertTo[Priority]).get,
            creator     = fieldMap.find(_._1 == "creator").map(_._2.convertTo[User]).get,
            createdAt   = fieldMap.find(_._1 == "created").map(_._2.convertTo[DateTime]).get,
            updatedAt   = fieldMap.find(_._1 == "updated").map(_._2.convertTo[DateTime]).get,
            changeLogs  = Seq.empty[ChangeLog],
            attachments = fieldMap.find(_._1 == "attachment").map(_._2.convertTo[Seq[Attachment]]).getOrElse(Seq.empty[Attachment])
          )
        }
        case other => deserializationError("Cannot deserialize Issue: invalid input. Raw input: " + other)
      }
    }
  }
}
