package com.nulabinc.backlog.j2b.jira.converter

import com.nulabinc.backlog.j2b.jira.domain.mapping.ValidatedJiraPriorityMapping

trait PriorityConverter {

  def convert(mappings: Seq[ValidatedJiraPriorityMapping], priorityName: String): String

}
