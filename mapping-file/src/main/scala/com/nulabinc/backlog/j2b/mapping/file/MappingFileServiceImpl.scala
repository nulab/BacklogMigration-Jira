package com.nulabinc.backlog.j2b.mapping.file

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.jira.domain.MappingFile
import com.nulabinc.backlog.j2b.jira.service.MappingFileService
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration
import com.nulabinc.jira.client.domain.{Priority, Status, User}

class MappingFileServiceImpl @Inject()(jiraApiConfig: JiraApiConfiguration,
                                       backlogApiConfig: BacklogApiConfiguration)
    extends MappingFileService {

  override def createUserMappingFile(users: Set[User]): MappingFile =
    new UserMappingFile(jiraApiConfig, backlogApiConfig, users.toSeq)

  override def createPriorityMappingFile(priorities: Seq[Priority]): MappingFile =
    new PriorityMappingFile(jiraApiConfig, backlogApiConfig, priorities)

  override def createStatusMappingFile(statuses: Seq[Status]): MappingFile =
    new StatusMappingFile(jiraApiConfig, backlogApiConfig, statuses)
}
