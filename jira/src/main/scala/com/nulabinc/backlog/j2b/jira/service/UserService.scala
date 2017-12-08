package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.jira.client.domain.User

trait UserService {

  def allUsers(): Seq[User]

  def optUserOfKey(key: Option[String]): Option[User]

  def optUserOfName(name: Option[String]): Option[User]

}
