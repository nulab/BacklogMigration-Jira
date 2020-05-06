package com.nulabinc.jira.client.json

import java.text.SimpleDateFormat

import com.nulabinc.jira.client.domain.Version
import spray.json._

object VersionMappingJsonProtocol extends DefaultJsonProtocol {

  private val format = new SimpleDateFormat("yyyy-MM-dd")

  implicit object VersionMappingFormat extends RootJsonFormat[Version] {
    def write(obj: Version) = ???

    def read(json: JsValue) = {
      val jsObject = json.asJsObject

      val description = jsObject.getFields("description") match {
        case Seq(JsString(str)) => Some(str)
        case _                  => None
      }

      val releaseDate = jsObject.getFields("releaseDate") match {
        case Seq(JsString(str)) => Option(str).map(format.parse)
        case _                  => None
      }

      jsObject.getFields("id", "name", "archived", "released") match {
        case Seq(
              JsString(id),
              JsString(name),
              JsBoolean(archived),
              JsBoolean(released)
            ) =>
          Version(
            id = Option(id.toLong),
            name = name,
            description = description,
            archived = archived,
            released = released,
            releaseDate = releaseDate
          )
        case other =>
          deserializationError(
            "Cannot deserialize Version: invalid input. Raw input: " + other
          )
      }
    }
  }

  implicit val versionResultJsonFormat = jsonFormat1(VersionResult)

  private[client] case class VersionResult(values: Seq[Version])

}
