package com.nulabinc.backlog.j2b.exporter

import com.nulabinc.jira.client.domain._

private [exporter] class IssueInitialValue(fieldType: String, fieldId: FieldId) {

  def findJournalDetail(journals: Seq[ChangeLog]): Option[ChangeLogItem] =
    journals.find(isTargetJournal).flatMap(targetJournalDetail)

  def findJournalDetails(journals: Seq[ChangeLog]): Option[Seq[ChangeLogItem]] =
    journals.find(isTargetJournal).map(targetJournalDetails)

  private def targetJournalDetail(journal: ChangeLog): Option[ChangeLogItem] =
    journal.items.find(isTargetJournalDetail)

  private def isTargetJournal(journal: ChangeLog): Boolean =
    journal.items.exists(isTargetJournalDetail)

  private def isTargetJournalDetail(detail: ChangeLogItem): Boolean =
    detail.fieldId.contains(fieldId) && detail.fieldType == fieldType

  private def targetJournalDetails(journal: ChangeLog): Seq[ChangeLogItem] =
    journal.items.filter(isTargetJournalDetail).filter(detail => detail.from.isDefined)

}
