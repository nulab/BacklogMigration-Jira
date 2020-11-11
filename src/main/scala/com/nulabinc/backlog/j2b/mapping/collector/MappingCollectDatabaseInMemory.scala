package com.nulabinc.backlog.j2b.mapping.collector

import com.nulabinc.backlog.j2b.jira.domain.`export`.{
  ChangeLogMappingUser,
  ExistingMappingUser,
  MappingUser
}
import com.nulabinc.backlog.j2b.jira.domain.export.Milestone
import com.nulabinc.backlog.j2b.jira.domain.mapping.{CustomFieldRow, MappingCollectDatabase}

import scala.collection.mutable

class MappingCollectDatabaseInMemory extends MappingCollectDatabase {

  private val userSet        = mutable.Set.empty[ExistingMappingUser]
  private val ignoreUserSet  = mutable.Set.empty[ChangeLogMappingUser]
  private val customFieldSet = mutable.Set.empty[CustomFieldRow]
  private val milestoneSet   = mutable.Set.empty[Milestone]

  override def addUser(user: ExistingMappingUser): ExistingMappingUser = {
    userSet += user
    user
  }

  override def addChangeLogUser(
      user: ChangeLogMappingUser
  ): ChangeLogMappingUser = {
    ignoreUserSet += user
    user
  }

  override def existUsers: Set[MappingUser] =
    userSet.toSet ++ ignoreUserSet.toSet

  override def findUser(accountId: String): Option[MappingUser] =
    existUsers.find(_.key == accountId)

  override def addCustomField(
      fieldId: String,
      value: Option[String]
  ): Option[String] =
    value.map { v =>
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

  override def customFieldRows: Seq[CustomFieldRow] =
    customFieldSet.toSeq

  override def addMilestone(milestone: Milestone): Unit =
    milestoneSet += milestone

  override def milestones: Seq[Milestone] =
    milestoneSet.toSeq

}
