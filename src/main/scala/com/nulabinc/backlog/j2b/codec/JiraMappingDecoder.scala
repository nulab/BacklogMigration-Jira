package com.nulabinc.backlog.j2b.codec

import com.nulabinc.backlog.j2b.jira.domain.mapping._
import com.nulabinc.backlog.migration.common.codec.{
  PriorityMappingDecoder,
  StatusMappingDecoder,
  UserMappingDecoder
}
import com.nulabinc.backlog.migration.common.domain.mappings._
import org.apache.commons.csv.CSVRecord

object JiraMappingDecoder {
  implicit val statusDeserializer: StatusMappingDecoder[JiraStatusMappingItem] =
    (record: CSVRecord) =>
      new StatusMapping[JiraStatusMappingItem] {
        override val src: JiraStatusMappingItem =
          JiraStatusMappingItem(record.get(0), record.get(0))
        override val srcDisplayValue: String = src.display
        override val optDst: Option[BacklogStatusMappingItem] =
          Option(record.get(1)).map(s => BacklogStatusMappingItem(s))
      }

  implicit val priorityDeserializer: PriorityMappingDecoder[JiraPriorityMappingItem] =
    (record: CSVRecord) =>
      new PriorityMapping[JiraPriorityMappingItem] {
        override val src: JiraPriorityMappingItem = JiraPriorityMappingItem(
          record.get(0)
        )
        override val srcDisplayValue: String = src.value
        override val optDst: Option[BacklogPriorityMappingItem] =
          Option(record.get(1)).map(p => BacklogPriorityMappingItem(p))
      }

  implicit val userDeserializer: UserMappingDecoder[JiraUserMappingItem] =
    (record: CSVRecord) =>
      new UserMapping[JiraUserMappingItem] {
        override val src: JiraUserMappingItem =
          JiraUserMappingItem(record.get(0), record.get(1))
        override val srcDisplayValue: String =
          src.displayName
        override val optDst: Option[BacklogUserMappingItem] =
          Option(record.get(2)).map(BacklogUserMappingItem)
        override val mappingType: String =
          record.get(3)
      }
}
