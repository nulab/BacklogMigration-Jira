package com.nulabinc.backlog.j2b.jira.writer

import com.nulabinc.backlog.migration.common.domain.BacklogCustomFieldSetting
import com.nulabinc.jira.client.domain.field.Field

trait FieldWriter {

  def write(fields: Seq[Field]): Either[WriteError, Seq[BacklogCustomFieldSetting]]

}
