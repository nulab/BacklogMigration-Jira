package com.nulabinc.backlog.j2b.jira.domain.mapping

import com.nulabinc.jira.client.domain.User

trait MappingCollectDatabase {

  def add(user: Option[User]): Boolean

  def add(user: User): User

  def add(name: Option[String]): Option[User]

  def existUsers: Set[User]

  def existsByName(name: Option[String]): Boolean

  def findByName(name: Option[String]): Option[User]
}
