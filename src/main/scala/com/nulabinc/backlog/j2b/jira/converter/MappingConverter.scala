package com.nulabinc.backlog.j2b.jira.converter

import com.nulabinc.backlog.j2b.jira.domain.mapping.{Mapping, MappingCollectDatabase}

trait MappingConverter {

  def convert(database: MappingCollectDatabase, userMaps: Seq[Mapping], priorityMaps: Seq[Mapping], statusMaps: Seq[Mapping]): Unit
}
