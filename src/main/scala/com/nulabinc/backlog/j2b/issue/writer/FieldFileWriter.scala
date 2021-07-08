package com.nulabinc.backlog.j2b.issue.writer

import com.nulabinc.backlog.j2b.issue.writer.convert.FieldWrites
import com.nulabinc.backlog.j2b.jira.domain.FieldDefinitions
import com.nulabinc.backlog.j2b.jira.domain.export.Field
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingCollectDatabase
import com.nulabinc.backlog.j2b.jira.writer.FieldWriter
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain.BacklogCustomFieldSettings
import com.nulabinc.backlog.migration.common.utils.IOUtil
import javax.inject.Inject
import spray.json._

class FieldFileWriter @Inject() (implicit
    val fieldWrites: FieldWrites,
    backlogPaths: BacklogPaths
) extends FieldWriter {

  import com.nulabinc.backlog.migration.common.formatters.BacklogJsonProtocol._

  override def write(db: MappingCollectDatabase, fields: Seq[Field]) = {
    val fieldDefinitions = FieldDefinitions(fields, db.customFieldRows)
    val backlogFields = Convert.toBacklog(fieldDefinitions)
    IOUtil.output(
      backlogPaths.customFieldSettingsJson,
      BacklogCustomFieldSettings(backlogFields).toJson.prettyPrint
    )
    Right(backlogFields)
  }

}
