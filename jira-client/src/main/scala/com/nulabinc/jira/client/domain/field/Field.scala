package com.nulabinc.jira.client.domain.field

case class Field(
  id: String,
  name: String,
  schema: Option[FieldSchema]
)