package com.nulabinc.backlog.j2b.jira.service

import better.files.{File => Path}

import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingFile
import com.nulabinc.backlog.migration.common.domain.BacklogUser
import com.nulabinc.jira.client.domain.{Priority, Status, User}
import com.nulabinc.backlog4j.{Priority => BacklogPriority, Status => BacklogStatus}

trait MappingFileService {

  def createUserMappingFile(users: Set[User], backlogUsers: Seq[BacklogUser]): MappingFile

  def createPriorityMappingFile(priorities: Seq[Priority], backlogPriorities: Seq[BacklogPriority]): MappingFile

  def createStatusMappingFile(statuses: Seq[Status], backlogStatuses: Seq[BacklogStatus]): MappingFile

  def createUserMappingFileFromJson(jiraUsersFilePath: Path, backlogUsers: Seq[BacklogUser]): MappingFile

  def createPrioritiesMappingFileFromJson(jiraPrioritiesFilePath: Path, backlogPriorities: Seq[BacklogPriority]): MappingFile

  def createStatusesMappingFileFromJson(jiraStatusesFilePath: Path, backlogStatuses: Seq[BacklogStatus]): MappingFile

  def usersFromJson(jiraUsersFilePath: Path): Seq[User]
}
