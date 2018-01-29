package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.field._
import spray.json._

object FieldMappingJsonProtocol extends DefaultJsonProtocol {

  implicit object FieldSchemaMappingFormat extends RootJsonFormat[FieldType] {
    def write(obj: FieldType) = ???

    def read(json: JsValue): FieldType = {
      val jsObject = json.asJsObject

      val schemaType = jsObject.getFields("type") match {
        case Seq(JsString(name)) => Some(name)
        case _                       => None
      }

      val schemaSystem = jsObject.getFields("system") match {
        case Seq(JsString(name)) => Some(name)
        case _                       => None
      }

      val schemaItems = jsObject.getFields("items") match {
        case Seq(JsString(name)) => Some(name)
        case _                       => None
      }

//      val customId = jsObject.getFields("customId") match {
//        case Seq(JsString(id)) => Some(id.toLong)
//        case _ => None
//      }
//
//      val customType = jsObject.getFields("custom") match {
//        case Seq(JsString(id)) => FieldCustomType.convert(id)
//        case _ => None
//      }

      FieldType(schemaType = schemaType, schemaSystem = schemaSystem, schemaItems = schemaItems)
    }


  }

  implicit object fieldMappingFormat extends RootJsonFormat[Field] {
    override def write(obj: Field): JsValue = ???

    override def read(json: JsValue): Field = {
      val jsObject = json.asJsObject

      jsObject.getFields("id", "name", "schema") match {
        case Seq(JsString(id), JsString(name), schema) => Field(
          id = id,
          name = name,
          schema = schema.convertTo[FieldType]
        )
        case _ => deserializationError(s"Not supported schema")
      }
    }

  }

}
