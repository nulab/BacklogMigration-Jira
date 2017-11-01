package com.nulabinc.backlog.j2b.jira.converter

import com.nulabinc.backlog.migration.common.domain.BacklogUser

trait UserConverter {

  def convert(user: BacklogUser): BacklogUser

}
