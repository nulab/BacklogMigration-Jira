package com.nulabinc.backlog.j2b.jira.converter

import com.nulabinc.backlog.j2b.jira.domain.mapping.ValidatedJiraUserMapping
import com.nulabinc.backlog.migration.common.domain.BacklogUser

trait UserConverter {

  def convert(mappings: Seq[ValidatedJiraUserMapping], user: String): String

  def convert(mappings: Seq[ValidatedJiraUserMapping], user: BacklogUser): BacklogUser

}
