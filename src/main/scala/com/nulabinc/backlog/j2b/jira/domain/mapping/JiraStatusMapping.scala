package com.nulabinc.backlog.j2b.jira.domain.mapping

import com.nulabinc.backlog.migration.common.domain.mappings.{BacklogStatusMappingItem, StatusMapping}

case class JiraStatusMappingItem(value: String, display: String)

case class JiraStatusMapping(
  src: JiraStatusMappingItem,
  optDst: Option[BacklogStatusMappingItem]
) extends StatusMapping[JiraStatusMappingItem]
