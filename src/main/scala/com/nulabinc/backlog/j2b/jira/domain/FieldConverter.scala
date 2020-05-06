package com.nulabinc.backlog.j2b.jira.domain

import com.nulabinc.backlog.j2b.jira.domain.export.{
  FieldType,
  Field => ExportField
}
import com.nulabinc.jira.client.domain.field.{Field => ClientField}

object FieldConverter {

  def toExportField(clientFields: Seq[ClientField]): Seq[ExportField] =
    for {
      field <- clientFields
      schema <- field.schema
    } yield {
      ExportField(
        id = field.id,
        name = field.name,
        schema = FieldType(
          schemaType = schema.`type`,
          schemaSystem = schema.system,
          schemaItems = schema.items,
          schemaCustom = schema.custom
        )
      )
    }

}
