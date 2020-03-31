package com.nulabinc.backlog.j2b.modules

import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.issue.writer.ProjectUserFileWriter
import com.nulabinc.backlog.j2b.jira.converter._
import com.nulabinc.backlog.j2b.jira.writer.ProjectUserWriter
import com.nulabinc.backlog.j2b.mapping.converter._
import com.nulabinc.backlog.j2b.mapping.converter.writes.UserWrites

class ImportModule(config: AppConfiguration) extends DefaultModule(config) {

  override def configure(): Unit = {
    super.configure()

    // Writes
    bind(classOf[UserWrites]).toInstance(new UserWrites)

    // Converter
    bind(classOf[PriorityConverter]).to(classOf[MappingPriorityConverter])

    // Writer
    bind(classOf[ProjectUserWriter]).to(classOf[ProjectUserFileWriter])

  }
}
