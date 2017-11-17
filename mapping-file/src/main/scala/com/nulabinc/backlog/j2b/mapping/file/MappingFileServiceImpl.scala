package com.nulabinc.backlog.j2b.mapping.file

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.jira.domain.mapping.{Mapping, MappingFile}
import com.nulabinc.backlog.j2b.jira.service.MappingFileService
import com.nulabinc.jira.client.domain.{Priority, Status => JiraStatus, User => JiraUser}
import com.nulabinc.backlog.migration.common.modules.{ServiceInjector => BacklogInjector}
import com.nulabinc.backlog.migration.common.service.{StatusService => BacklogStatusService}
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration
import com.nulabinc.backlog4j.{Status => BacklogStatus, User => BacklogUser}

class MappingFileServiceImpl @Inject()(jiraApiConfig: JiraApiConfiguration,
                                       backlogApiConfig: BacklogApiConfiguration)
    extends MappingFileService {

  override def createUserMappingFile(users: Set[JiraUser]): MappingFile =
    new UserMappingFile(jiraApiConfig, backlogApiConfig, users.toSeq)

  override def createPriorityMappingFile(priorities: Seq[Priority]): MappingFile =
    new PriorityMappingFile(jiraApiConfig, backlogApiConfig, priorities)

  override def createStatusMappingFile(jiraStatuses: Seq[JiraStatus]): MappingFile = {
    val injector        = BacklogInjector.createInjector(backlogApiConfig)
    val statusService   = injector.getInstance(classOf[BacklogStatusService])
    val backlogStatuses = statusService.allStatuses()

    new StatusMappingFile(jiraStatuses, backlogStatuses)
  }

  override def userMappingsFromFile() = ???
//  {
//    val mappingFile = new UserMappingFile(Seq.empty[JiraUser], Seq.empty[BacklogUser])
//    mappingFile.tryUnMarshal()
//  }

  override def priorityMappingsFromFile() = ???

  override def statusMappingsFromFile(): Seq[Mapping] = {
    val mappingFile = new StatusMappingFile(Seq.empty[JiraStatus], Seq.empty[BacklogStatus])
    mappingFile.tryUnMarshal()
  }
}
