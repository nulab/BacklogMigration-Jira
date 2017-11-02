package com.nulabinc.backlog.j2b.jira.converter

import com.nulabinc.backlog.j2b.jira.domain.Mapping

trait MappingConverter {

  def convert(userMaps: Seq[Mapping], priorityMaps: Seq[Mapping], statusMaps: Seq[Mapping]): Unit
}
