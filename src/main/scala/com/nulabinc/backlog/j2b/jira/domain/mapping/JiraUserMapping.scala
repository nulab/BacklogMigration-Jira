package com.nulabinc.backlog.j2b.jira.domain.mapping

import com.nulabinc.backlog.j2b.jira.domain.`export`.MappingUser
import com.nulabinc.backlog.migration.common.domain.mappings.{BacklogUserMappingItem, UserMappingType, ValidatedUserMapping}

case class JiraUserMappingItem(accountId: String, displayName: String)

object JiraUserMappingItem {
  def from(user: MappingUser): JiraUserMappingItem =
    JiraUserMappingItem(accountId = user.key, displayName = user.displayName)
}

case class ValidatedJiraUserMapping(
  src: JiraUserMappingItem,
  dst: BacklogUserMappingItem,
  mappingType: UserMappingType
) extends ValidatedUserMapping[JiraUserMappingItem]

object ValidatedJiraUserMapping {
  def from(mapping: ValidatedUserMapping[JiraUserMappingItem]): ValidatedJiraUserMapping =
    ValidatedJiraUserMapping(
      src = mapping.src,
      dst = mapping.dst,
      mappingType = mapping.mappingType
    )
}