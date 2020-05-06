package com.nulabinc.backlog.j2b.exporter

import com.nulabinc.jira.client.domain.changeLog.{
  ChangeLog,
  ChangeLogItem,
  FieldId
}

private[exporter] class IssueInitialValue(fieldType: String, fieldId: FieldId) {

  def findChangeLogItem(changeLogs: Seq[ChangeLog]): Option[ChangeLogItem] =
    changeLogs.find(isTargetChangeLog).flatMap(targetChangeLogItem)

  def findChangeLogItems(
      changeLogs: Seq[ChangeLog]
  ): Option[Seq[ChangeLogItem]] =
    changeLogs.find(isTargetChangeLog).map(targetChangeLogItems)

  private def targetChangeLogItem(changeLog: ChangeLog): Option[ChangeLogItem] =
    changeLog.items.find(isTargetChangeLogItem)

  private def isTargetChangeLog(changeLog: ChangeLog): Boolean =
    changeLog.items.exists(isTargetChangeLogItem)

  private def isTargetChangeLogItem(changeLogItem: ChangeLogItem): Boolean =
    changeLogItem.fieldId.contains(
      fieldId
    ) && changeLogItem.fieldType == fieldType

  private def targetChangeLogItems(changeLog: ChangeLog): Seq[ChangeLogItem] =
    changeLog.items
      .filter(isTargetChangeLogItem)
      .filter(changeLogItem => changeLogItem.fromDisplayString.isDefined)

}
