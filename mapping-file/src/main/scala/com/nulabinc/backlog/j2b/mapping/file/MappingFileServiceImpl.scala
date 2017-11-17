package com.nulabinc.backlog.j2b.mapping.file

import javax.inject.Inject

import com.nulabinc.jira.client.domain.{Priority => JiraPriority, Status => JiraStatus, User => JiraUser}
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.jira.domain.mapping.{Mapping, MappingFile}
import com.nulabinc.backlog.j2b.jira.service.MappingFileService
import com.nulabinc.backlog.migration.common.modules.{ServiceInjector => BacklogInjector}
import com.nulabinc.backlog.migration.common.service.{PriorityService => BacklogPriorityService, StatusService => BacklogStatusService, UserService => BacklogUserService}
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration
import com.nulabinc.backlog.migration.common.domain.BacklogUser
import com.nulabinc.backlog4j.{Priority => BacklogPriority, Status => BacklogStatus}

class MappingFileServiceImpl @Inject()(jiraApiConfig: JiraApiConfiguration,
                                       backlogApiConfig: BacklogApiConfiguration)
    extends MappingFileService {

  override def createUserMappingFile(users: Set[JiraUser]): MappingFile = {
    val injector      = BacklogInjector.createInjector(backlogApiConfig)
    val userService   = injector.getInstance(classOf[BacklogUserService])
    val backlogUsers  = userService.allUsers()

    new UserMappingFile(backlogApiConfig, users.toSeq, backlogUsers)
  }

  override def createPriorityMappingFile(jiraPriorities: Seq[JiraPriority]): MappingFile = {
    val injector          = BacklogInjector.createInjector(backlogApiConfig)
    val priorityService   = injector.getInstance(classOf[BacklogPriorityService])
    val backlogPriorities = priorityService.allPriorities()

    new PriorityMappingFile(jiraPriorities, backlogPriorities)
  }

  override def createStatusMappingFile(jiraStatuses: Seq[JiraStatus]): MappingFile = {
    val injector        = BacklogInjector.createInjector(backlogApiConfig)
    val statusService   = injector.getInstance(classOf[BacklogStatusService])
    val backlogStatuses = statusService.allStatuses()

    new StatusMappingFile(jiraStatuses, backlogStatuses)
  }

  override def userMappingsFromFile() = {
    val mappingFile = new UserMappingFile(backlogApiConfig, Seq.empty[JiraUser], Seq.empty[BacklogUser])
    mappingFile.tryUnMarshal()
  }

  override def priorityMappingsFromFile(): Seq[Mapping] = {
    val mappingFile = new PriorityMappingFile(Seq.empty[JiraPriority], Seq.empty[BacklogPriority])
    mappingFile.tryUnMarshal()
  }

  override def statusMappingsFromFile(): Seq[Mapping] = {
    val mappingFile = new StatusMappingFile(Seq.empty[JiraStatus], Seq.empty[BacklogStatus])
    mappingFile.tryUnMarshal()
  }
}
