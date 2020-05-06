package com.nulabinc.backlog.j2b.jira.utils

import com.nulabinc.backlog.j2b.jira.domain.export.{Field, FieldType}
import com.nulabinc.jira.client.domain.changeLog.ChangeLog

trait DateChangeLogConverter extends DatetimeToDateFormatter {

  def convertDateChangeLogs(
      changeLogs: Seq[ChangeLog],
      definitions: Seq[Field]
  ): Seq[ChangeLog] = {
    changeLogs.map { changeLog =>
      val items = changeLog.items.map { item =>
        definitions.find(f => item.fieldId.exists(_.value == f.id)) match {
          case Some(field) =>
            field.schema match {
              case FieldType.Date =>
                item.copy(
                  fromDisplayString = item.from,
                  toDisplayString = item.to
                )
              case FieldType.DateTime =>
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
