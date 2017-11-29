package com.nulabinc.backlog.j2b.mapping.collector

import com.nulabinc.backlog.j2b.jira.domain.mapping.{CustomFieldRow, MappingCollectDatabase}
import com.nulabinc.jira.client.domain.User

import scala.collection.mutable

class MappingCollectDatabaseInMemory extends MappingCollectDatabase {

  private val userSet: mutable.Set[User] = mutable.Set[User]()
  private val customFieldSet = mutable.Set.empty[CustomFieldRow]

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


  override def addCustomField(fieldId: String, value: Option[String]): Option[String] = value.map { v =>
    val items = v.split(",").map(_.trim).filter(_.nonEmpty)
    customFieldSet.find(_.fieldId == fieldId) match {
      case Some(row) =>
        items.map(str => row.values.add(str))
        v
      case None =>
        customFieldSet += CustomFieldRow(fieldId, mutable.Set() ++ items)
        v
    }
  }

  override def customFieldRows: Seq[CustomFieldRow] = customFieldSet.toSeq

  override def findCustomFieldValues(fieldId: String): Seq[String] = customFieldSet
      .find(_.fieldId == fieldId).map(_.values.toSeq)
      .getOrElse(Seq.empty[String])

}
