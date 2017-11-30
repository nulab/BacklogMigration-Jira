package com.nulabinc.backlog.j2b.jira.utils

import com.nulabinc.jira.client.domain.changeLog.{ChangeLog, StatusChangeLogItemField}
import com.nulabinc.jira.client.domain.field.Field

trait DateChangeLogConverter {

  def convertDateChangeLogs(changeLogs: Seq[ChangeLog], definitions: Seq[Field]): Seq[ChangeLog] = {
    changeLogs.map { changeLog =>
      val items = changeLog.items.map { item =>
        definitions.find(f => item.fieldId.exists(_.value == f.id)) match {
          case Some(_) =>
            item.copy(
              fromDisplayString = item.from,
              toDisplayString = item.to
            )
          case _ => item
        }
      }
      changeLog.copy(items = items)
    }
  }

}
