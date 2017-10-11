package com.nulabinc.backlog.j2b.jira.service

import com.atlassian.jira.rest.client.api.domain.User

trait UserService {

  def allUsers(): Seq[User]

  def tryUserOfId(id: String): User

  def optUserOfId(id: String): Option[User]

}
