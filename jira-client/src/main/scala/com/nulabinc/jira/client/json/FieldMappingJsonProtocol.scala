package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain._
import spray.json._

object FieldMappingJsonProtocol extends DefaultJsonProtocol {

  implicit object FieldSchemaMappingFormat extends RootJsonFormat[FieldSchema] {
    def write(obj: FieldSchema) = ???

    def read(json: JsValue) = {
      val jsObject = json.asJsObject
      jsObject.getFields("customId", "type") match {
        case Seq(JsNumber(id), JsString(typeName)) =>
          FieldSchema(Some(id.toLong), FieldSchemaType.convert(typeName))
        case Seq(JsString(typeName)) =>
          FieldSchema(None, FieldSchemaType.convert(typeName))
        case other =>
          deserializationError("Cannot deserialize FieldSchema: invalid input. Raw input: " + other)
      }
    }
  }

  implicit val fieldMappingFormat = jsonFormat3(Field)

}
