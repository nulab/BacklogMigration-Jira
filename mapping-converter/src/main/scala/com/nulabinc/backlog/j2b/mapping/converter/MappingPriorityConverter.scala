package com.nulabinc.backlog.j2b.mapping.converter

import com.nulabinc.backlog.j2b.jira.converter.{MappingConverter, PriorityConverter}
import com.nulabinc.backlog.j2b.jira.domain.mapping.Mapping

class MappingPriorityConverter extends PriorityConverter {

  override def convert(mappings: Seq[Mapping], priorityName: String) =
    if (mappings.isEmpty) priorityName
    else
      mappings.find(_.src == priorityName) match {
        case Some(mapping) =>
          if (mapping.dst.nonEmpty) mapping.dst else priorityName
        case _ => priorityName
      }
}
