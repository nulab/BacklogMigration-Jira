package com.nulabinc.backlog.j2b.jira.domain.mapping

import com.nulabinc.backlog.migration.common.domain.mappings.{BacklogPriorityMappingItem, PriorityMapping}

case class JiraPriorityMappingItem(value: String)

case class JiraPriorityMapping(
  src: JiraPriorityMappingItem,
  optDst: Option[BacklogPriorityMappingItem]
) extends PriorityMapping[JiraPriorityMappingItem]
