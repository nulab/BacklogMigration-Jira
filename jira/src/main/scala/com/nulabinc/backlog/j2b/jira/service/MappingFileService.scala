package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.jira.client.domain.{Priority, User}

trait MappingFileService {

  def outputUserMappingFile(users: Set[User]): Unit

  def outputPriorityMappingFile(priorities: Seq[Priority]): Unit
}
