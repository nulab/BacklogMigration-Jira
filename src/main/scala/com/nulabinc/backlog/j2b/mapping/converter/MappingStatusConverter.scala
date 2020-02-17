package com.nulabinc.backlog.j2b.mapping.converter

import com.nulabinc.backlog.j2b.jira.domain.mapping.Mapping
import com.nulabinc.backlog.migration.common.domain.{BacklogCustomStatus, BacklogStatus, BacklogStatusName}

object MappingStatusConverter {

  def convert(mappings: Seq[Mapping], value: BacklogStatus): BacklogStatus =
    if (mappings.isEmpty) value
    else
      mappings.find(_.src == value.name.trimmed) match {
        case Some(mapping) if mapping.dst.nonEmpty => BacklogCustomStatus.create(BacklogStatusName(mapping.dst))
        case Some(_) => value
        case _ => value
      }

}
