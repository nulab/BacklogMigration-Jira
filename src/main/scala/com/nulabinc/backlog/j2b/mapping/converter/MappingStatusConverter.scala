package com.nulabinc.backlog.j2b.mapping.converter

import com.nulabinc.backlog.j2b.jira.domain.mapping.Mapping
import com.nulabinc.backlog.migration.common.domain.{BacklogCustomStatus, BacklogStatus, BacklogStatusName}

object MappingStatusConverter {

  def convert(mappings: Seq[Mapping], value: String): BacklogStatus =
    if (mappings.isEmpty) BacklogCustomStatus.create(BacklogStatusName(value))
    else
      mappings.find(_.src == value) match {
        case Some(mapping) if mapping.dst.nonEmpty => BacklogCustomStatus.create(BacklogStatusName(mapping.dst))
        case _ => BacklogCustomStatus.create(BacklogStatusName(value))
      }

  def convert(mappings: Seq[Mapping], value: BacklogStatus): BacklogStatus =
    if (mappings.isEmpty) value
    else
      mappings.find(_.src == value.name.trimmed) match {
        case Some(mapping) if mapping.dst.nonEmpty => BacklogCustomStatus.create(BacklogStatusName(mapping.dst))
        case _ => value
      }

}
