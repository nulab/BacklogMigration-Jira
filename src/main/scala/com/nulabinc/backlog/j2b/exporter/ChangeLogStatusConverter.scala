package com.nulabinc.backlog.j2b.exporter

import com.nulabinc.jira.client.domain.Status
import com.nulabinc.jira.client.domain.changeLog.{
  ChangeLog,
  StatusChangeLogItemField
}

object ChangeLogStatusConverter {

  def convert(
      changeLogs: Seq[ChangeLog],
      statuses: Seq[Status]
  ): Seq[ChangeLog] = {
    changeLogs.map { changeLog =>
      val items = changeLog.items.map { item =>
        item.field match {
          case StatusChangeLogItemField =>
            item.copy(
              fromDisplayString = item.from.flatMap(from =>
                statuses.find(_.id == from).map(_.name)
              ),
              toDisplayString =
                item.to.flatMap(to => statuses.find(_.id == to).map(_.name))
            )
          case _ => item
        }
      }
      changeLog.copy(items = items)
    }
  }
}
