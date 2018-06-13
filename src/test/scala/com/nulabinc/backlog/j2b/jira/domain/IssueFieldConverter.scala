package com.nulabinc.backlog.j2b.jira.domain

import com.nulabinc.backlog.j2b.jira.domain.export.{FieldValue, StringFieldValue, IssueField => ExportIssueField}
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.jira.client.domain.issue.{IssueField => ClientIssueField}
import spray.json._

object IssueFieldConverter extends Logging {

  import com.nulabinc.backlog.j2b.jira.domain.export.JsonFormatters._

  def toExportIssueFields(clientIssueFields: Seq[ClientIssueField]): Seq[ExportIssueField] =
    clientIssueFields.map { clientIssueField =>
      val value = try {
        clientIssueField.value.parseJson.convertTo[FieldValue]
      } catch {
        case e: Throwable =>
          logger.warn(e.getMessage)
          StringFieldValue(clientIssueField.value)
      }
      ExportIssueField(clientIssueField.id, value)
    }

}
