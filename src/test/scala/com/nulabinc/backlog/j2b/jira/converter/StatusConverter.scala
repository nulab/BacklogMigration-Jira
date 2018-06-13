package com.nulabinc.backlog.j2b.jira.converter

import com.nulabinc.backlog.j2b.jira.domain.mapping.Mapping

trait StatusConverter {

  def convert(mappings: Seq[Mapping], value: String): String

}
