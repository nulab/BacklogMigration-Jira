package com.nulabinc.backlog.j2b.serializers

import com.nulabinc.backlog.j2b.jira.domain.mapping.JiraStatusMappingItem
import com.nulabinc.backlog.migration.common.domain.mappings.{Serializer, StatusMapping}

object JiraMappingSerializer {
  implicit val statusSerializer: Serializer[StatusMapping[JiraStatusMappingItem], Seq[String]] =
    (mapping: StatusMapping[JiraStatusMappingItem]) =>
      Seq(mapping.src.value, mapping.optDst.map(_.value).getOrElse(""))
}
