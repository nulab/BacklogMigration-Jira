package com.nulabinc.backlog.j2b.mapping.file

import better.files.{File => Path}
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingFile
import com.nulabinc.backlog.j2b.jira.service.MappingFileService
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration
import com.nulabinc.backlog.migration.common.domain.BacklogUser
import com.nulabinc.backlog.migration.common.utils.IOUtil
import com.nulabinc.backlog4j.{Priority => BacklogPriority, Status => BacklogStatus}
import com.nulabinc.jira.client.domain.{Priority => JiraPriority, Status => JiraStatus, User => JiraUser}
import javax.inject.Inject
import spray.json._

class MappingFileServiceImpl @Inject()(backlogApiConfig: BacklogApiConfiguration)
    extends MappingFileService {

  override def createUserMappingFile(jiraUsers: Set[JiraUser], backlogUsers: Seq[BacklogUser]): MappingFile =
    new UserMappingFile(backlogApiConfig, jiraUsers.toSeq, backlogUsers)

  override def createPriorityMappingFile(jiraPriorities: Seq[JiraPriority], backlogPriorities: Seq[BacklogPriority]): MappingFile =
    new PriorityMappingFile(jiraPriorities, backlogPriorities)

  override def createStatusMappingFile(jiraStatuses: Seq[JiraStatus], backlogStatuses: Seq[BacklogStatus]): MappingFile =
    new StatusMappingFile(jiraStatuses, backlogStatuses)

  override def createUserMappingFileFromJson(jiraUsersFilePath: Path, backlogUsers: Seq[BacklogUser]): MappingFile =
    new UserMappingFile(backlogApiConfig, usersFromJson(jiraUsersFilePath), backlogUsers)

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

  override def usersFromJson(jiraUsersFilePath: Path): Seq[JiraUser] = {
    import com.nulabinc.jira.client.json.UserMappingJsonProtocol._
    JsonParser(IOUtil.input(jiraUsersFilePath).get).convertTo[Seq[JiraUser]]
  }
}
