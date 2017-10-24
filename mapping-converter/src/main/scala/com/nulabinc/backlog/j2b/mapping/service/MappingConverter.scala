package com.nulabinc.backlog.j2b.mapping.service

import com.nulabinc.backlog.j2b.mapping.domain.Mapping

trait MappingConverter {

  def convert(mappings: Seq[Mapping], target: String): String =
    if (mappings.isEmpty) target
    else
      mappings.find(_.jira == target) match {
        case Some(mapping) =>
          if (mapping.backlog.nonEmpty) mapping.backlog else target
        case _ => target
      }
}