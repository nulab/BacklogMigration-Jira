package com.nulabinc.jira.client.json

import java.text.SimpleDateFormat
import java.util.Date

import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.changeLog.ChangeLog
import com.nulabinc.jira.client.domain.issue._
import spray.json._

import scala.util.Try

object IssueMappingJsonProtocol extends DefaultJsonProtocol {

  import UserMappingJsonProtocol._
  import TimeTrackMappingJsonProtocol._
  import IssueTypeMappingJsonProtocol._
  import ComponentMappingJsonProtocol._
  import StatusMappingJsonProtocol._
  import PriorityMappingJsonProtocol._
  import VersionMappingJsonProtocol._
  import AttachmentMappingJsonProtocol._

  implicit object DateFormat extends JsonFormat[Date] {
    def write(date: Date) = ???
    def read(json: JsValue) =
      json match {
        case JsString(rawDate) =>
          parseIsoDateString(rawDate).fold(
            deserializationError(s"Expected ISO Date format, got $rawDate")
          )(identity)
        case error => deserializationError(s"Expected JsString, got $error")
      }

    private val localIsoDateFormatter = new ThreadLocal[SimpleDateFormat] {
      override def initialValue() = new SimpleDateFormat("yyyy-MM-dd")
    }

    private def parseIsoDateString(date: String): Option[Date] =
      Try { localIsoDateFormatter.get().parse(date) }.toOption
  }

  implicit object ParentIssueMappingFormat extends RootJsonFormat[ParentIssue] {
    def write(obj: ParentIssue): JsValue = ???

    def read(json: JsValue): ParentIssue =
      json.asJsObject.getFields("id") match {
        case Seq(JsString(id)) => ParentIssue(id.toLong)
        case other =>
          deserializationError(
            "Cannot deserialize ParentIssue: invalid input. Raw input: " + other
          )
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
            .map { item => IssueField(item._1, item._2.toString) }
            .toSeq

          def requireField[A](fields: Map[String, JsValue], fieldName: String)(implicit
              p: JsonReader[A]
          ): A =
            fields.find(_._1 == fieldName).map(_._2) match {
              case Some(result) if result == JsNull =>
                deserializationError(
                  s"Cannot deserialize Issue. Field: $fieldName is required. Key: $key"
                )
              case Some(result) =>
                result.convertTo[A]
              case None =>
                deserializationError(
                  s"Cannot deserialize Issue. Field: $fieldName not found. Key: $key"
                )
            }

          Issue(
            id = id.toLong,
            key = key,
            summary = fieldMap.find(_._1 == "summary").map(_._2.convertTo[String]).getOrElse(""),
            description = fieldMap
              .find(_._1 == "description")
              .filterNot(_._2 == JsNull)
              .map(_._2.convertTo[String]),
            parent = fieldMap.find(_._1 == "parent").map(_._2.convertTo[ParentIssue]),
            assignee = fieldMap
              .find(_._1 == "assignee")
              .filterNot(_._2 == JsNull)
              .map(_._2.convertTo[User]),
            components = fieldMap
              .find(_._1 == "components")
              .map(_._2.convertTo[Seq[Component]])
              .getOrElse(Seq.empty[Component]),
            fixVersions = fieldMap
              .find(_._1 == "fixVersions")
              .map(_._2.convertTo[Seq[Version]])
              .getOrElse(Seq.empty[Version]),
            issueFields = issueFields,
            dueDate =
              fieldMap.find(_._1 == "duedate").filterNot(_._2 == JsNull).map(_._2.convertTo[Date]),
            timeTrack = fieldMap.find(_._1 == "timetracking").map(_._2.convertTo[TimeTrack]),
            issueType = requireField[IssueType](fieldMap, "issuetype"),
            status = requireField[Status](fieldMap, "status"),
            priority = requireField[Priority](fieldMap, "priority"),
            creator = requireField[User](fieldMap, "creator"),
            createdAt = requireField[Date](fieldMap, "created"),
            updatedAt = requireField[Date](fieldMap, "updated"),
            changeLogs = Seq.empty[ChangeLog],
            attachments = fieldMap
              .find(_._1 == "attachment")
              .map(_._2.convertTo[Seq[Attachment]])
              .getOrElse(Seq.empty[Attachment])
          )
        }
        case other =>
          deserializationError(
            "Cannot deserialize Issue: invalid input. Raw input: " + other
          )
      }
    }
  }
}
