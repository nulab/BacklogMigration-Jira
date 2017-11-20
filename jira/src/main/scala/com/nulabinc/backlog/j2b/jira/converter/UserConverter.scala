package com.nulabinc.backlog.j2b.jira.converter

import com.nulabinc.backlog.j2b.jira.domain.mapping.Mapping
import com.nulabinc.backlog.migration.common.domain.BacklogUser

trait UserConverter {

  def convert(mappings: Seq[Mapping], user: String): String

  def convert(mappings: Seq[Mapping], user: BacklogUser): BacklogUser

}
