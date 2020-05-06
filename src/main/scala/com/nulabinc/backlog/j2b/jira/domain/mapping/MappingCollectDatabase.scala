package com.nulabinc.backlog.j2b.jira.domain.mapping

import com.nulabinc.backlog.j2b.jira.domain.`export`.{
  ChangeLogMappingUser,
  ExistingMappingUser,
  MappingUser
}
import com.nulabinc.backlog.j2b.jira.domain.export.Milestone

import scala.collection.mutable

case class CustomFieldRow(fieldId: String, values: mutable.Set[String])

trait MappingCollectDatabase {

  def addUser(user: ExistingMappingUser): ExistingMappingUser

  def addChangeLogUser(user: ChangeLogMappingUser): ChangeLogMappingUser

  def findUser(accountId: String): Option[MappingUser]

  def existUsers: Set[MappingUser]

  def addCustomField(fieldId: String, value: Option[String]): Option[String]

  def customFieldRows: Seq[CustomFieldRow]

  def addMilestone(milestone: Milestone): Unit

  def milestones: Seq[Milestone]
}
