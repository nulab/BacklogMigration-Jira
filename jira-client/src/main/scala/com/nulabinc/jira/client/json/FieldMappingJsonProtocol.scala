package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.field._
import spray.json._

object FieldMappingJsonProtocol extends DefaultJsonProtocol {

  implicit val fieldSchemaJsonFormat = jsonFormat3(FieldSchema)
  implicit val fieldJsonFormat = jsonFormat3(Field)
  
}
