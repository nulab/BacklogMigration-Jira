package com.nulabinc.backlog.j2b.jira.domain.mapping

import com.nulabinc.jira.client.domain.User

import scala.collection.mutable

case class CustomFieldRow(fieldId: String, values: mutable.Set[String])

trait MappingCollectDatabase {

  def add(user: Option[User]): Boolean

  def add(user: User): User

  def add(name: Option[String]): Option[User]

  def existUsers: Set[User]

  def existsByName(name: Option[String]): Boolean

  def findByName(name: Option[String]): Option[User]

  def addCustomField(fieldId: String, value: Option[String]): Option[String]

  def customFieldRows: Seq[CustomFieldRow]

  def findCustomFieldValues(fieldId: String): Seq[String]
}
