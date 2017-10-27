package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.field._
import spray.json._

object FieldMappingJsonProtocol extends DefaultJsonProtocol {

  implicit object FieldSchemaMappingFormat extends RootJsonFormat[FieldSchema] {
    def write(obj: FieldSchema) = ???

    def read(json: JsValue) = {
      val jsObject = json.asJsObject

      val schemaType = jsObject.getFields("type") match {
        case Seq(JsString(typeName)) => FieldSchemaType.convert(typeName)
        case other => deserializationError("Cannot deserialize FieldSchema: invalid input missing `type`. Raw input: " + other)
      }

      val customId = jsObject.getFields("customId") match {
        case Seq(JsString(id)) => Some(id.toLong)
        case _ => None
      }

      val customType = jsObject.getFields("custom") match {
        case Seq(JsString(id)) => FieldCustomType.convert(id)
        case _ => None
      }

      FieldSchema(
        customId = customId,
        customType = customType,
        schemaType = schemaType
      )
    }


  }

  implicit val fieldMappingFormat = jsonFormat3(Field)

}
