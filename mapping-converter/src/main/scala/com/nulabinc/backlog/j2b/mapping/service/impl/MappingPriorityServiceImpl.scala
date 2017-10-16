package com.nulabinc.backlog.j2b.mapping.service.impl

import com.nulabinc.backlog.j2b.mapping.domain.Mapping
import com.nulabinc.backlog.j2b.mapping.service.{MappingConverter, MappingPriorityService}

class MappingPriorityServiceImpl(mappings: Seq[Mapping]) extends MappingPriorityService with MappingConverter{

  override def convert(value: String) = convert(mappings, value)

}
