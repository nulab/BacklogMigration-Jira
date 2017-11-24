package com.nulabinc.backlog.j2b.exporter

import com.nulabinc.jira.client.domain.{Component, Version}
import com.nulabinc.jira.client.domain.changeLog._

object ChangeLogFilter {

  def filter(components: Seq[Component], versions: Seq[Version], changeLogs: Seq[ChangeLog]): Seq[ChangeLog] = {
    changeLogs.map { changeLog =>
      val items = changeLog.items.map { item =>
        item.field match {
          case ComponentChangeLogItemField =>
            List(
              item.from,
              item.to
            ).flatten.flatMap { value =>
              components.find(_.id == value.toLong)
            }.length match {
              case n if n > 0 => item
              case _          => item.copy(field = DefaultField("deleted_category"), fieldId = None)
            }
          case FixVersion =>
            List(
              item.from,
              item.to
            ).flatten.flatMap { value =>
              versions.find(_.id == value.toLong)
            }.length match {
              case n if n > 0 => item
              case _          => item.copy(field = DefaultField("deleted_version"), fieldId = None)
            }
          case _ => item
        }
      }
      changeLog.copy(items = items)
    }
  }
}
