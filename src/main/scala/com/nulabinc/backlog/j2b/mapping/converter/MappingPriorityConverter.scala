package com.nulabinc.backlog.j2b.mapping.converter

import com.nulabinc.backlog.j2b.jira.domain.mapping.ValidatedJiraPriorityMapping

object MappingPriorityConverter {

  def convert(mappings: Seq[ValidatedJiraPriorityMapping], priorityName: String): String =
    if (mappings.isEmpty) priorityName
    else mappings.find(_.src.value == priorityName).map(_.dst.value).getOrElse(priorityName)

}
