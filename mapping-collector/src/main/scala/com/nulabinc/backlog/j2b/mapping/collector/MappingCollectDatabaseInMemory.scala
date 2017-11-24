package com.nulabinc.backlog.j2b.mapping.collector

import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingCollectDatabase
import com.nulabinc.jira.client.domain.User

import scala.collection.mutable

class MappingCollectDatabaseInMemory extends MappingCollectDatabase {

  private case class UserExt(name: String, displayName: String, exists: Boolean)

  private var userSet: mutable.Set[UserExt] = mutable.Set[UserExt]()

  override def add(user: Option[User]): Boolean = user match {
    case Some(u) => userSet += UserExt(u.name, u.displayName, true); true
    case None    => false
  }

  override def add(user: User): User = {
    userSet += UserExt(user.name, user.displayName, true)
    user
  }

  override def addDeletedUser(name: Option[String]): Unit = name.map(n => userSet += UserExt(n, "", false))

  override def existUsers: Set[User] = userSet.toSet.filter(_.exists).map(u => User(u.name, u.displayName))

  override def existsByName(name: Option[String]): Boolean = name match {
    case Some(n) => userSet.exists(_.name == n)
    case None    => false
  }

  override def findByName(name: Option[String]) = name match {
    case Some(_)  => userSet.find(_.name == name).map(u => User(u.name, u.displayName))
    case None     => None
  }
}
