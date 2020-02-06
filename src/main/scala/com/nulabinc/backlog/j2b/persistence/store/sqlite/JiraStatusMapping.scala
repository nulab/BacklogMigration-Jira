package com.nulabinc.backlog.j2b.persistence.store.sqlite

import com.nulabinc.backlog.migration.common.domain.Types.AnyId
import com.nulabinc.backlog.migration.common.domain.mappings.{BacklogStatusMappingItem, StatusMapping}

case class JiraStatusMappingItem(value: String) extends AnyVal

case class JiraStatusMapping(
  id: AnyId,
  optSrc: Option[JiraStatusMappingItem],
  optDst: Option[BacklogStatusMappingItem]
) extends StatusMapping[JiraStatusMappingItem]

object JiraStatusMapping {
  val tupled = (this.apply _).tupled
}
