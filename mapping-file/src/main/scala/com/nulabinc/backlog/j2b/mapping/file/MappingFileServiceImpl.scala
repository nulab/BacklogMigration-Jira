package com.nulabinc.backlog.j2b.mapping.file

import javax.inject.Inject

import com.nulabinc.jira.client.domain.{Priority => JiraPriority, Status => JiraStatus, User => JiraUser}
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingFile
import com.nulabinc.backlog.j2b.jira.service.MappingFileService
import com.nulabinc.backlog.migration.common.modules.{ServiceInjector => BacklogInjector}
import com.nulabinc.backlog.migration.common.service.{PriorityService => BacklogPriorityService, StatusService => BacklogStatusService, UserService => BacklogUserService}
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration
import com.nulabinc.backlog.migration.common.domain.BacklogUser
import com.nulabinc.backlog.migration.common.utils.IOUtil
import com.nulabinc.backlog4j.{Status => BacklogStatus, Priority => BacklogPriority}

import scalax.file.Path
import spray.json._

class MappingFileServiceImpl @Inject()(jiraApiConfig: JiraApiConfiguration,
                                       backlogApiConfig: BacklogApiConfiguration)
    extends MappingFileService {

  override def createUserMappingFile(jiraUsers: Set[JiraUser], backlogUsers: Seq[BacklogUser]): MappingFile =
    new UserMappingFile(backlogApiConfig, jiraUsers.toSeq, backlogUsers)

  override def createPriorityMappingFile(jiraPriorities: Seq[JiraPriority], backlogPriorities: Seq[BacklogPriority]): MappingFile =
    new PriorityMappingFile(jiraPriorities, backlogPriorities)

  override def createStatusMappingFile(jiraStatuses: Seq[JiraStatus], backlogStatuses: Seq[BacklogStatus]): MappingFile =
    new StatusMappingFile(jiraStatuses, backlogStatuses)

  override def createUserMappingFileFromJson(jiraUsersFilePath: Path, backlogUsers: Seq[BacklogUser]): MappingFile = {
    import com.nulabinc.jira.client.json.UserMappingJsonProtocol._
    val jiraUsers = JsonParser(IOUtil.input(jiraUsersFilePath).get).convertTo[Seq[JiraUser]]

    new UserMappingFile(backlogApiConfig, jiraUsers, backlogUsers)
  }

  override def createPrioritiesMappingFileFromJson(jiraPrioritiesFilePath: Path, backlogPriorities: Seq[BacklogPriority]): PriorityMappingFile = {
    import com.nulabinc.jira.client.json.PriorityMappingJsonProtocol._
    val jiraPriorities = JsonParser(IOUtil.input(jiraPrioritiesFilePath).get).convertTo[Seq[JiraPriority]]

    new PriorityMappingFile(jiraPriorities, backlogPriorities)
  }

  override def createStatusesMappingFileFromJson(jiraStatusesFilePath: Path, backlogStatuses: Seq[BacklogStatus]): StatusMappingFile = {
    import com.nulabinc.jira.client.json.StatusMappingJsonProtocol._
    val jiraStatuses = JsonParser(IOUtil.input(jiraStatusesFilePath).get).convertTo[Seq[JiraStatus]]

    new StatusMappingFile(jiraStatuses, backlogStatuses)
  }
}
