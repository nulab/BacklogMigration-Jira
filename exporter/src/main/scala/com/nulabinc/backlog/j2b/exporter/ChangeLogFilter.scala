package com.nulabinc.backlog.j2b.exporter

import com.nulabinc.backlog.j2b.jira.domain.export.Field
import com.nulabinc.backlog.j2b.jira.domain.export.FieldType.CustomLabels
import com.nulabinc.jira.client.domain.{Component, Version}
import com.nulabinc.jira.client.domain.changeLog._
import com.nulabinc.jira.client.domain.field.CustomLabel

object ChangeLogFilter {

  def filter(definitions: Seq[Field], components: Seq[Component], versions: Seq[Version], changeLogs: Seq[ChangeLog]): Seq[ChangeLog] = {
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
          case LinkChangeLogItemField => item.copy(field = DefaultField("link_issue"), fieldId = None)
          case _ => item.field match {
            case DefaultField(fieldId) => definitions.find(_.name == fieldId) match {
              case Some(definition) if definition.schema == CustomLabels =>
                item.copy(
                  fromDisplayString = item.fromDisplayString.map(_.replace(" ", ",")),
                  toDisplayString = item.toDisplayString.map(_.replace(" ", ","))
                )
              case _ => item
            }
            case _ => item
          }
        }
      }
      changeLog.copy(items = items.filterNot(_.field == WorkIdChangeLogItemField))
    }
  }
}
