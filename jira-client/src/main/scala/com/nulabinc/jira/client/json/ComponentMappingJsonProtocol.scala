package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.Component
import spray.json._

object ComponentMappingJsonProtocol extends DefaultJsonProtocol {

  implicit object ComponentMappingFormat extends RootJsonFormat[Component] {
    def write(obj: Component) = ???

    def read(json: JsValue) = {
      val jsObject = json.asJsObject
      jsObject.getFields("id", "name") match {
        case Seq(JsString(id), JsString(name)) => Component(id.toLong, name)
        case other => deserializationError("Cannot deserialize Component: invalid input. Raw input: " + other)
      }
    }
  }

  implicit val componentResultJsonFormat = jsonFormat1(ComponentResult)

  private [client] case class ComponentResult(values: Seq[Component])

}
