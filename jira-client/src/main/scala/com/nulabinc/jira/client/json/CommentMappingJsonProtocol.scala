package com.nulabinc.jira.client.json

import java.util.Date

import com.nulabinc.jira.client.domain._
import spray.json._

object CommentMappingJsonProtocol extends DefaultJsonProtocol {

  import UserMappingJsonProtocol._
  import DateTimeMappingJsonProtocol._

  implicit object CommentMappingFormat extends RootJsonFormat[Comment] {
    def write(obj: Comment) = ???

    def read(json: JsValue) = {
      val jsObject = json.asJsObject
      jsObject.getFields("id", "body", "author", "created") match {
        case Seq(JsString(id), JsString(body), author, createdAt) =>
          Comment(
            id = id.toLong,
            body = body,
            author = author.convertTo[User],
            createdAt = createdAt.convertTo[Date]
          )
        case other =>
          deserializationError(
            "Cannot deserialize Comment: invalid input. Raw input: " + other
          )
      }
    }
  }

  implicit val commentResultJsonFormat = jsonFormat3(CommentResult)

}
