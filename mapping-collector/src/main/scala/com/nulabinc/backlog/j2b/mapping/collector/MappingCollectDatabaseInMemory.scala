package com.nulabinc.backlog.j2b.mapping.collector

import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingCollectDatabase
import com.nulabinc.jira.client.domain.User

import scala.collection.mutable

class MappingCollectDatabaseInMemory extends MappingCollectDatabase {

  private val userSet: mutable.Set[User] = mutable.Set[User]()

  override def add(user: Option[User]): Boolean = user match {
    case Some(u) =>
      userSet += u
      true
    case None => false
  }

  override def add(user: User): User = {
    userSet += user
    user
  }

  override def add(name: Option[String]) = name match {
    case Some(n) =>
      val user = User(n, n)
      userSet += user
      Some(user)
    case None => None
  }


  override def existUsers: Set[User] = userSet.toSet

  override def existsByName(name: Option[String]): Boolean = name match {
    case Some(n) => userSet.exists(_.name == n)
    case None    => false
  }

  override def findByName(name: Option[String]): Option[User] = name match {
    case Some(_)  => userSet.find(_.name == name)
    case None     => None
  }

}
