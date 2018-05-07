package com.nulabinc.jira.client.json

import java.util.Date

import com.nulabinc.jira.client.domain.{Attachment, User}
import spray.json._

object AttachmentMappingJsonProtocol extends DefaultJsonProtocol {

  import UserMappingJsonProtocol._
  import DateTimeMappingJsonProtocol._

  implicit object AttachmentMappingFormat extends RootJsonFormat[Attachment] {
    def write(obj: Attachment): JsValue = ???

    def read(json: JsValue): Attachment =
      json.asJsObject.getFields("id", "filename", "author", "created", "size", "mimeType", "content") match {
        case Seq(JsString(id), JsString(fileName), author, createdAt, JsNumber(size), JsString(mineType), JsString(content)) =>
          Attachment(
            id = id.toLong,
            fileName = fileName,
            author = author.convertTo[User],
            createdAt = createdAt.convertTo[Date],
            size = size.toLong,
            mimeType = mineType,
            content = content
          )
        case other => deserializationError("Cannot deserialize Attachment: invalid input. Raw input: " + other)
      }
  }

}
