package com.nulabinc.backlog.j2b.modules

import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.jira.converter._
import com.nulabinc.backlog.j2b.jira.service.MappingFileService
import com.nulabinc.backlog.j2b.mapping.converter._
import com.nulabinc.backlog.j2b.mapping.converter.writes.UserWrites
import com.nulabinc.backlog.j2b.mapping.file.MappingFileServiceImpl

class ImportModule(config: AppConfiguration) extends DefaultModule(config) {

  override def configure(): Unit = {
    super.configure()

    // Writes
    bind(classOf[UserWrites]).toInstance(new UserWrites)

    // Converter
    bind(classOf[UserConverter]).to(classOf[MappingUserConverter])
    bind(classOf[PriorityConverter]).to(classOf[MappingPriorityConverter])
    bind(classOf[StatusConverter]).to(classOf[MappingStatusConverter])

    // Mapping-converter
    bind(classOf[MappingConverter]).to(classOf[MappingConvertService])
  }
}
