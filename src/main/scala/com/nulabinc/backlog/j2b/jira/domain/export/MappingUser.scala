package com.nulabinc.backlog.j2b.jira.domain.`export`

import com.nulabinc.backlog.migration.common.conf.BacklogConstantValue
import com.nulabinc.backlog.migration.common.domain.BacklogUser

sealed trait MappingUser {
  val key: String
  val displayName: String

  def toBacklogUser: BacklogUser =
    this match {
      case u: ExistingMappingUser =>
        BacklogUser(
          optId = None,
          optUserId = Some(u.key),
          optPassword = None,
          name = displayName,
          optMailAddress = u.optEmail,
          roleType = BacklogConstantValue.USER_ROLE
        )
      case u: ChangeLogMappingUser =>
        BacklogUser(
          optId = None,
          optUserId = Some(u.key),
          optPassword = None,
          name = displayName,
          optMailAddress = None,
          roleType = BacklogConstantValue.USER_ROLE
        )
    }
}

case class ExistingMappingUser(
    key: String,
    displayName: String,
    optEmail: Option[String]
) extends MappingUser
case class ChangeLogMappingUser(key: String, displayName: String)
    extends MappingUser
