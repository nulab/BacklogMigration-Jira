package com.nulabinc.backlog.j2b.mapping.service

import com.nulabinc.backlog.j2b.jira.converter.{MappingConverter, MappingUserService}
import com.nulabinc.backlog.j2b.jira.domain.Mapping

class MappingUserServiceImpl(mappings: Seq[Mapping]) extends MappingUserService with MappingConverter {

  override def convert(value: String) = convert(mappings, value)

}
