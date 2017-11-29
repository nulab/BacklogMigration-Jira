package com.nulabinc.backlog.j2b.issue.writer

import javax.inject.Inject

import com.nulabinc.backlog.j2b.issue.writer.convert.FieldWrites
import com.nulabinc.backlog.j2b.jira.domain.FieldDefinition
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingCollectDatabase
import com.nulabinc.backlog.j2b.jira.writer.FieldWriter
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain.BacklogCustomFieldSettingsWrapper
import com.nulabinc.backlog.migration.common.utils.IOUtil
import com.nulabinc.jira.client.domain.field.Field
import spray.json._

class FieldFileWriter @Inject()(implicit val fieldWrites: FieldWrites,
                                backlogPaths: BacklogPaths) extends FieldWriter {

  import com.nulabinc.backlog.migration.common.domain.BacklogJsonProtocol._

  override def write(db: MappingCollectDatabase, fields: Seq[Field]) = {
    val fieldDefinitions = FieldDefinition(fields, db.customFieldRows)
    val backlogFields = Convert.toBacklog(fieldDefinitions)
    IOUtil.output(backlogPaths.customFieldSettingsJson, BacklogCustomFieldSettingsWrapper(backlogFields).toJson.prettyPrint)
    Right(backlogFields)
  }

}
