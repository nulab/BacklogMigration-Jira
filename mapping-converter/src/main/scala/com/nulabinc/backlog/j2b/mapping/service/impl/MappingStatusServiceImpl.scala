package com.nulabinc.backlog.j2b.mapping.service.impl

import com.nulabinc.backlog.j2b.mapping.domain.Mapping
import com.nulabinc.backlog.j2b.mapping.service.{MappingConverter, MappingStatusService}

class MappingStatusServiceImpl(mappings: Seq[Mapping]) extends MappingStatusService with MappingConverter {

  override def convert(value: String) = convert(mappings, value)

}
