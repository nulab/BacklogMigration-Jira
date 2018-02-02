package com.nulabinc.backlog.j2b.issue.writer.convert

import com.nulabinc.backlog.migration.common.conf.BacklogConstantValue
import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain.BacklogUser
import com.nulabinc.jira.client.domain.User

class UserWrites extends Writes[User, BacklogUser] {

  override def writes(user: User) =
    BacklogUser(
      optId = None,
      optUserId = Some(user.identifyKey), // TODO: check impl. user.key?
      optPassword = None,
      name = user.name,
      optMailAddress = Some(user.emailAddress),
      roleType = BacklogConstantValue.USER_ROLE
    )
}
