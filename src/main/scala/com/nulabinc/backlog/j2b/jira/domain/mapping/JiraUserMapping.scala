package com.nulabinc.backlog.j2b.jira.domain.mapping

import com.nulabinc.backlog.j2b.jira.domain.`export`.MappingUser
import com.nulabinc.backlog.migration.common.domain.mappings.{BacklogUserMappingItem, UserMapping}

case class JiraUserMappingItem(accountId: String, displayName: String)

object JiraUserMappingItem {
  def from(user: MappingUser): JiraUserMappingItem =
    JiraUserMappingItem(accountId = user.key, displayName = user.displayName)
}

case class JiraUserMapping(
  src: JiraUserMappingItem,
  optDst: Option[BacklogUserMappingItem]
) extends UserMapping[JiraUserMappingItem]
