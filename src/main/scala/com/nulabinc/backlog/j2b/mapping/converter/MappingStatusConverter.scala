package com.nulabinc.backlog.j2b.mapping.converter

import com.nulabinc.backlog.j2b.jira.domain.mapping.ValidatedJiraStatusMapping
import com.nulabinc.backlog.migration.common.domain.{BacklogCustomStatus, BacklogStatus, BacklogStatusName}

object MappingStatusConverter {

  def convert(
      mappings: Seq[ValidatedJiraStatusMapping],
      value: String
  ): BacklogStatus =
    if (mappings.isEmpty)
      BacklogCustomStatus.create(BacklogStatusName(value))
    else
      findFromMappings(mappings, value).getOrElse(
        BacklogCustomStatus.create(BacklogStatusName(value))
      )

  def convert(
      mappings: Seq[ValidatedJiraStatusMapping],
      value: BacklogStatus
  ): BacklogStatus =
    if (mappings.isEmpty) value
    else
      findFromMappings(mappings, value.name.trimmed).getOrElse(value)

  private def findFromMappings(
      mappings: Seq[ValidatedJiraStatusMapping],
      value: String
  ): Option[BacklogStatus] =
    for {
      mapping <- mappings.find(_.src.value == value)
    } yield BacklogCustomStatus.create(BacklogStatusName(mapping.dst.value))
}
