package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.backlog.j2b.jira.domain.mapping.{Mapping, MappingFile}
import com.nulabinc.jira.client.domain.{Priority, Status, User}

trait MappingFileService {

  def createUserMappingFile(users: Set[User]): MappingFile

  def createPriorityMappingFile(priorities: Seq[Priority]): MappingFile

  def createStatusMappingFile(statuses: Seq[Status]): MappingFile

  def userMappingsFromFile(): Seq[Mapping]

  def priorityMappingsFromFile(): Seq[Mapping]

  def statusMappingsFromFile(): Seq[Mapping]
}
