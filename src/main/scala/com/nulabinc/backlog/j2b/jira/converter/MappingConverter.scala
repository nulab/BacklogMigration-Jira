package com.nulabinc.backlog.j2b.jira.converter

import com.nulabinc.backlog.j2b.jira.domain.mapping.{ValidatedJiraStatusMapping, Mapping, MappingCollectDatabase}

trait MappingConverter {

  def convert(database: MappingCollectDatabase, userMaps: Seq[Mapping], priorityMaps: Seq[Mapping], statusMaps: Seq[ValidatedJiraStatusMapping]): Unit
}
