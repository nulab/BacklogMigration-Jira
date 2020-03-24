package com.nulabinc.backlog.j2b.mapping.converter

import com.nulabinc.backlog.j2b.jira.converter.PriorityConverter
import com.nulabinc.backlog.j2b.jira.domain.mapping.ValidatedJiraPriorityMapping

class MappingPriorityConverter extends PriorityConverter {

  override def convert(mappings: Seq[ValidatedJiraPriorityMapping], priorityName: String): String =
    if (mappings.isEmpty) priorityName
    else mappings.find(_.src.value == priorityName).map(_.dst.value).getOrElse(priorityName)

}
