package com.nulabinc.backlog.j2b.mapping.file

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.jira.service.MappingFileService
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration
import com.nulabinc.jira.client.domain.{Priority, User}

class MappingFileServiceImpl @Inject()(jiraApiConfig: JiraApiConfiguration,
                                       backlogApiConfig: BacklogApiConfiguration)
    extends MappingFileService {

  override def outputUserMappingFile(users: Set[User]): Unit = {

    val file = new UserMappingFile(jiraApiConfig, backlogApiConfig, users.toSeq)

    file.create()
  }

  override def outputPriorityMappingFile(priorities: Seq[Priority]): Unit = {
    val file = new PriorityMappingFile(jiraApiConfig, backlogApiConfig, priorities)
    file.create()
  }
}
