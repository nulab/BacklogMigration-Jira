package com.nulabinc.backlog.j2b.deserializers

import com.nulabinc.backlog.j2b.jira.domain.mapping.JiraStatusMappingItem
import com.nulabinc.backlog.migration.common.domain.mappings.{BacklogStatusMappingItem, Deserializer, StatusMapping}
import org.apache.commons.csv.CSVRecord

object JiraMappingDeserializer {
  implicit val statusDeserializer: Deserializer[CSVRecord, StatusMapping[JiraStatusMappingItem]] =
    (record: CSVRecord) => new StatusMapping[JiraStatusMappingItem] {
      override val src: JiraStatusMappingItem = JiraStatusMappingItem(record.get(0), record.get(0))
      override val optDst: Option[BacklogStatusMappingItem] = Option(record.get(1)).map(s => BacklogStatusMappingItem(s))
    }
}
