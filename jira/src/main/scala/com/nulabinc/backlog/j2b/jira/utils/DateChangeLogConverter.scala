package com.nulabinc.backlog.j2b.jira.utils

import com.nulabinc.jira.client.domain.changeLog.ChangeLog
import com.nulabinc.jira.client.domain.field.{DateSchema, DatetimeSchema, Field}

trait DateChangeLogConverter extends DatetimeToDateFormatter {

  def convertDateChangeLogs(changeLogs: Seq[ChangeLog], definitions: Seq[Field]): Seq[ChangeLog] = {
    changeLogs.map { changeLog =>
      val items = changeLog.items.map { item =>
        definitions.find(f => item.fieldId.exists(_.value == f.id)) match {
          case Some(field) => field.schema match {
            case Some(schema) if schema.schemaType == DateSchema =>
              item.copy(
                fromDisplayString = item.from,
                toDisplayString = item.to
              )
            case Some(schema) if schema.schemaType == DatetimeSchema =>
              item.copy(
                fromDisplayString = item.from.map(dateTimeStringToDateString),
                toDisplayString = item.to.map(dateTimeStringToDateString)
              )
            case _ => item
          }
          case _ => item
        }
      }
      changeLog.copy(items = items)
    }
  }

}
