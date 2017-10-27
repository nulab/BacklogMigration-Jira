package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.Version
import org.joda.time.DateTime
import spray.json._

object VersionMappingJsonProtocol extends DefaultJsonProtocol {

  implicit object VersionMappingFormat extends RootJsonFormat[Version] {
    def write(obj: Version) = ???

    def read(json: JsValue) = {
      val jsObject = json.asJsObject
      jsObject.getFields("id", "name", "description", "archived", "released", "releaseDate") match {
        case Seq(JsString(id), JsString(name), JsString(description), JsBoolean(archived), JsBoolean(released), JsString(releaseDate)) =>
          Version(
            id = Option(id.toLong),
            name = name,
            description = description,
            archived = archived,
            released = released,
            releaseDate = Option(releaseDate).map(DateTime.parse)
          )
        case Seq(JsString(id), JsString(name), JsString(description), JsBoolean(archived), JsBoolean(released)) =>
          Version(
            id = Option(id.toLong),
            name = name,
            description = description,
            archived = archived,
            released = released,
            releaseDate = None
          )
        case other => deserializationError("Cannot deserialize Version: invalid input. Raw input: " + other)
      }
    }
  }

  implicit val versionResultJsonFormat = jsonFormat1(VersionResult)

  private [client] case class VersionResult(values: Seq[Version])

}
