package com.nulabinc.backlog.j2b.mapping.converter.writes

import com.nulabinc.backlog.j2b.jira.domain.Mapping
import com.nulabinc.backlog.migration.common.conf.BacklogConstantValue
import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain.BacklogUser

class UserWrites extends Writes[Mapping, BacklogUser] {

  override def writes(mapping: Mapping) =
    BacklogUser(optId = None,
      optUserId = Some(mapping.dst),
      optPassword = None,
      name = mapping.src,
      optMailAddress = None,
      roleType = BacklogConstantValue.USER_ROLE)

}
