package com.nulabinc.backlog.j2b.jira.writer

import com.nulabinc.backlog.j2b.jira.domain.export.Field
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingCollectDatabase
import com.nulabinc.backlog.migration.common.domain.BacklogCustomFieldSetting

trait FieldWriter {

  def write(db: MappingCollectDatabase, fields: Seq[Field]): Either[WriteError, Seq[BacklogCustomFieldSetting]]

}
