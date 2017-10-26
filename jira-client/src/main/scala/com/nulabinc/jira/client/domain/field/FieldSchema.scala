package com.nulabinc.jira.client.domain.field

case class FieldSchema(
  customId: Option[Long],
  customType: Option[FieldCustomType],
  schemaType: FieldSchemaType
)