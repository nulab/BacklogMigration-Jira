package com.nulabinc.backlog.j2b.mapping.converter.writes

import com.nulabinc.backlog.j2b.jira.domain.mapping.ValidatedJiraUserMapping
import com.nulabinc.backlog.migration.common.conf.BacklogConstantValue
import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain.BacklogUser
import javax.inject.Inject

class MappingUserWrites @Inject() () extends Writes[ValidatedJiraUserMapping, BacklogUser] {

  override def writes(mapping: ValidatedJiraUserMapping): BacklogUser = {
    BacklogUser(
      optId = None,
      optUserId = Some(mapping.dst.value),
      optPassword = None,
      name = mapping.src.displayName,
      optMailAddress = None,
      roleType = BacklogConstantValue.USER_ROLE
    )
  }

}
