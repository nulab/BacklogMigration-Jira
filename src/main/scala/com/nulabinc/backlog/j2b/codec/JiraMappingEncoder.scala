package com.nulabinc.backlog.j2b.codec

import com.nulabinc.backlog.j2b.jira.domain.mapping._
import com.nulabinc.backlog.migration.common.codec.{
  PriorityMappingEncoder,
  StatusMappingEncoder,
  UserMappingEncoder
}
import com.nulabinc.backlog.migration.common.domain.mappings._

object JiraMappingEncoder {
  implicit val statusSerializer: StatusMappingEncoder[JiraStatusMappingItem] =
    (mapping: StatusMapping[JiraStatusMappingItem]) =>
      Seq(mapping.src.value, mapping.optDst.map(_.value).getOrElse(""))

  implicit val prioritySerializer: PriorityMappingEncoder[JiraPriorityMappingItem] =
    (mapping: PriorityMapping[JiraPriorityMappingItem]) =>
      Seq(mapping.src.value, mapping.optDst.map(_.value).getOrElse(""))

  implicit val userSerializer: UserMappingEncoder[JiraUserMappingItem] =
    (mapping: UserMapping[JiraUserMappingItem]) =>
      Seq(
        mapping.src.accountId,                     // 0
        mapping.src.displayName,                   // 1
        mapping.optDst.map(_.value).getOrElse(""), // 2
        mapping.mappingType                        // 3
      )

}
