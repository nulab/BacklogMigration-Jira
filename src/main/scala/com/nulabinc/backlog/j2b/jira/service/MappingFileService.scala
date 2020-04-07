package com.nulabinc.backlog.j2b.jira.service

import better.files.{File => Path}
import com.nulabinc.backlog.j2b.jira.domain.`export`.MappingUser
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingFile
import com.nulabinc.backlog.migration.common.domain.{BacklogStatuses, BacklogUser}
import com.nulabinc.backlog4j.{Priority => BacklogPriority}
import com.nulabinc.jira.client.domain.{Priority, Status, User}

trait MappingFileService {

  def createUserMappingFile(users: Set[User], backlogUsers: Seq[BacklogUser]): MappingFile

  def createPriorityMappingFile(priorities: Seq[Priority], backlogPriorities: Seq[BacklogPriority]): MappingFile

  def createStatusMappingFile(statuses: Seq[Status], backlogStatuses: BacklogStatuses): MappingFile

  def createUserMappingFileFromJson(jiraUsersFilePath: Path, backlogUsers: Seq[BacklogUser]): MappingFile

  def createPrioritiesMappingFileFromJson(jiraPrioritiesFilePath: Path, backlogPriorities: Seq[BacklogPriority]): MappingFile

  def createStatusesMappingFileFromJson(jiraStatusesFilePath: Path, backlogStatuses: BacklogStatuses): MappingFile

  def usersFromJson(jiraUsersFilePath: Path): Seq[MappingUser]
}
