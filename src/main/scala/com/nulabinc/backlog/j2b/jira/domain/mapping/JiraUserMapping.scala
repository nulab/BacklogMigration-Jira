package com.nulabinc.backlog.j2b.jira.domain.mapping

import com.nulabinc.backlog.migration.common.domain.mappings.{BacklogUserMappingItem, UserMapping}

case class JiraUserMappingItem(value: String)

case class JiraUserMapping(
  src: JiraUserMappingItem,
  optDst: Option[BacklogUserMappingItem]
) extends UserMapping[JiraUserMappingItem]
