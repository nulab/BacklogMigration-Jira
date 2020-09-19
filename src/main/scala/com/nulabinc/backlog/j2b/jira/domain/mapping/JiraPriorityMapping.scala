package com.nulabinc.backlog.j2b.jira.domain.mapping

import com.nulabinc.backlog.migration.common.domain.mappings.{BacklogPriorityMappingItem, ValidatedPriorityMapping}

case class JiraPriorityMappingItem(value: String)

case class ValidatedJiraPriorityMapping(
    src: JiraPriorityMappingItem,
    dst: BacklogPriorityMappingItem
) extends ValidatedPriorityMapping[JiraPriorityMappingItem] {
  override val srcDisplayValue: String = src.value
}

object ValidatedJiraPriorityMapping {
  def from(
      mapping: ValidatedPriorityMapping[JiraPriorityMappingItem]
  ): ValidatedJiraPriorityMapping =
    ValidatedJiraPriorityMapping(src = mapping.src, dst = mapping.dst)
}
