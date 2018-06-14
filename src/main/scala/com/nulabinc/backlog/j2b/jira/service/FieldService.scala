package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.jira.client.domain.field.Field

trait FieldService {

  def all(): Seq[Field]
}
