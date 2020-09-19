package com.nulabinc.backlog.j2b.jira.domain.mapping

import com.nulabinc.backlog.migration.common.domain.mappings.{BacklogStatusMappingItem, ValidatedStatusMapping}

case class JiraStatusMappingItem(value: String, display: String)

case class ValidatedJiraStatusMapping(
    src: JiraStatusMappingItem,
    dst: BacklogStatusMappingItem
) extends ValidatedStatusMapping[JiraStatusMappingItem] {
  override val srcDisplayValue: String = src.display
}

object ValidatedJiraStatusMapping {
  def from(
      mapping: ValidatedStatusMapping[JiraStatusMappingItem]
  ): ValidatedJiraStatusMapping =
    ValidatedJiraStatusMapping(src = mapping.src, dst = mapping.dst)
}
