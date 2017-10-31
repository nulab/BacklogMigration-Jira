package com.nulabinc.backlog.j2b.mapping.service

import com.nulabinc.backlog.j2b.jira.converter.{MappingConverter, MappingPriorityService}
import com.nulabinc.backlog.j2b.jira.domain.Mapping

class MappingPriorityServiceImpl(mappings: Seq[Mapping]) extends MappingPriorityService with MappingConverter{

  override def convert(value: String) = convert(mappings, value)

}
