package com.nulabinc.backlog.j2b.exporter

import com.nulabinc.backlog.migration.common.domain.BacklogIssue
import com.nulabinc.jira.client.domain.changeLog._

object ChangeLogIssueLinkConverter {

  def convert(changeLogs: Seq[ChangeLog], backlogIssue: BacklogIssue): Seq[ChangeLog] = {
    val displayStrings = changeLogs.flatMap { changeLog =>
      changeLog.items
        .filter { item => item.field == DefaultField("link_issue") }
        .flatMap { _.toDisplayString }
    }

    val linkedIssueChangeLogItems = if (displayStrings.nonEmpty) {
      val lastDescription = changeLogs.reverse.flatMap { changeLog =>
        changeLog.items.reverse.find(_.field == DescriptionChangeLogItemField)
      }.headOption.flatMap(_.toDisplayString)

      Seq(
        ChangeLog(
          id = 0,   // TODO: Check
          author = changeLogs.last.author,
          createdAt = changeLogs.last.createdAt,
          items = Seq(
            ChangeLogItem(
              field = DescriptionChangeLogItemField,
              fieldType = ChangeLogItem.FieldType.JIRA,
              fieldId = Some(DescriptionFieldId),
              from = None,
              fromDisplayString = lastDescription,
              to = None,
              toDisplayString = Some(lastDescription.getOrElse(backlogIssue.description) + "\n\n" + displayStrings.mkString("\n"))
            )
          )
        )
      )
    } else {
      Seq.empty[ChangeLog]
    }

    changeLogs ++ linkedIssueChangeLogItems
  }
}
