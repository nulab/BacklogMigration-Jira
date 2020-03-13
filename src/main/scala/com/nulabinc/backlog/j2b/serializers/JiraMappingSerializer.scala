package com.nulabinc.backlog.j2b.serializers

import com.nulabinc.backlog.j2b.jira.domain.mapping._
import com.nulabinc.backlog.migration.common.domain.mappings._

object JiraMappingSerializer {
  implicit val statusSerializer: Serializer[StatusMapping[JiraStatusMappingItem], Seq[String]] =
    (mapping: StatusMapping[JiraStatusMappingItem]) =>
      Seq(mapping.src.value, mapping.optDst.map(_.value).getOrElse(""))

  implicit val prioritySerializer: Serializer[PriorityMapping[JiraPriorityMappingItem], Seq[String]] =
    (mapping: PriorityMapping[JiraPriorityMappingItem]) =>
      Seq(mapping.src.value, mapping.optDst.map(_.value).getOrElse(""))

  implicit val userSerializer: Serializer[UserMapping[JiraUserMappingItem], Seq[String]] =
    (mapping: UserMapping[JiraUserMappingItem]) =>
      Seq(
        mapping.src.accountId,                // 0
        mapping.src.displayName,              // 1
        mapping.dst.optValue.getOrElse(""),   // 2
        mapping.dst.mappingType               // 3
      )

}
