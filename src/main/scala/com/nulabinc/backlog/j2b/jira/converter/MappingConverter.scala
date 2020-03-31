package com.nulabinc.backlog.j2b.jira.converter

import com.nulabinc.backlog.j2b.jira.domain.mapping.{MappingCollectDatabase, ValidatedJiraPriorityMapping, ValidatedJiraStatusMapping, ValidatedJiraUserMapping}

trait MappingConverter {

  def convert(database: MappingCollectDatabase, userMaps: Seq[ValidatedJiraUserMapping], priorityMaps: Seq[ValidatedJiraPriorityMapping], statusMaps: Seq[ValidatedJiraStatusMapping]): Unit
}
