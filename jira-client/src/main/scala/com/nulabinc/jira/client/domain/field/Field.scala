package com.nulabinc.jira.client.domain.field

case class Field(
  id: String,
  name: String,
  schema: Option[FieldSchema]
)

case class FieldSchema(
  `type`: Option[String],
  system: Option[String],
  items: Option[String],
  custom: Option[String]
)