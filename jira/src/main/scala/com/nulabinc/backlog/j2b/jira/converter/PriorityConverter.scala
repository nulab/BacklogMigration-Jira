package com.nulabinc.backlog.j2b.jira.converter

import com.nulabinc.backlog.j2b.jira.domain.Mapping

trait PriorityConverter {

  def convert(mappings: Seq[Mapping], priorityName: String): String

}
