package com.nulabinc.backlog.j2b.deserializers

import com.nulabinc.backlog.j2b.jira.domain.mapping._
import com.nulabinc.backlog.migration.common.domain.mappings._
import org.apache.commons.csv.CSVRecord

object JiraMappingDeserializer {
  implicit val statusDeserializer: Deserializer[CSVRecord, StatusMapping[JiraStatusMappingItem]] =
    (record: CSVRecord) => new StatusMapping[JiraStatusMappingItem] {
      override val src: JiraStatusMappingItem = JiraStatusMappingItem(record.get(0), record.get(0))
      override val optDst: Option[BacklogStatusMappingItem] = Option(record.get(1)).map(s => BacklogStatusMappingItem(s))
    }

  implicit val priorityDeserializer: Deserializer[CSVRecord, PriorityMapping[JiraPriorityMappingItem]] =
    (record: CSVRecord) => new PriorityMapping[JiraPriorityMappingItem] {
      override val src: JiraPriorityMappingItem = JiraPriorityMappingItem(record.get(0))
      override val optDst: Option[BacklogPriorityMappingItem] = Option(record.get(1)).map(p => BacklogPriorityMappingItem(p))
    }

  implicit val userDeserializer: Deserializer[CSVRecord, UserMapping[JiraUserMappingItem]] =
    (record: CSVRecord) => new UserMapping[JiraUserMappingItem] {
      override val src: JiraUserMappingItem = JiraUserMappingItem(record.get(0), record.get(1))
      override val dst: BacklogUserMappingItem = {
        val optValue = Option(record.get(2))
        val mappingType = record.get(3)

        mappingType match {
          case "id" => BacklogUserIdMappingItem(optValue)
          case "mail" => BacklogUserMailMappingItem(optValue)
          case others => throw new RuntimeException(s"Invalid user mapping type. Input: $others")
        }
      }
    }
}
