package com.nulabinc.backlog.j2b.mapping.service.impl

import com.nulabinc.backlog.j2b.mapping.domain.Mapping
import com.nulabinc.backlog.j2b.mapping.service.{MappingConverter, MappingUserService}

class MappingUserServiceImpl(mappings: Seq[Mapping]) extends MappingUserService with MappingConverter {

  override def convert(value: String) = convert(mappings, value)

}
