package com.nulabinc.backlog.j2b.mapping.converter

import com.nulabinc.backlog.j2b.jira.converter.StatusConverter
import com.nulabinc.backlog.j2b.jira.domain.mapping.Mapping

class MappingStatusConverter extends StatusConverter {

  override def convert(mappings: Seq[Mapping], value: String) =
    if (mappings.isEmpty) value
    else
      mappings.find(_.src == value) match {
        case Some(mapping) =>
          if (mapping.dst.nonEmpty) mapping.dst else value
        case _ => value
      }

}
