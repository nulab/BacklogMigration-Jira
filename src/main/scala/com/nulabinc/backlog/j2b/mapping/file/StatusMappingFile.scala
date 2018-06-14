package com.nulabinc.backlog.j2b.mapping.file

import com.nulabinc.backlog.j2b.jira.domain.mapping.{MappingFile, MappingItem}
import com.nulabinc.backlog.j2b.mapping.core.MappingDirectory
import com.nulabinc.backlog4j.{Status => BacklogStatus}
import com.nulabinc.jira.client.domain.{Status => JiraStatus}
import com.osinka.i18n.{Lang, Messages}

class StatusMappingFile(jiraStatuses: Seq[JiraStatus], backlogStatuses: Seq[BacklogStatus]) extends MappingFile {

  private[this] object Backlog {
    val OPEN_JA: String        = Messages("mapping.status.backlog.open")(Lang("ja"))
    val IN_PROGRESS_JA: String = Messages("mapping.status.backlog.in_progress")(Lang("ja"))
    val RESOLVED_JA: String    = Messages("mapping.status.backlog.resolved")(Lang("ja"))
    val CLOSED_JA: String      = Messages("mapping.status.backlog.closed")(Lang("ja"))
    val OPEN_EN: String        = Messages("mapping.status.backlog.open")(Lang("en"))
    val IN_PROGRESS_EN: String = Messages("mapping.status.backlog.in_progress")(Lang("en"))
    val RESOLVED_EN: String    = Messages("mapping.status.backlog.resolved")(Lang("en"))
    val CLOSED_EN: String      = Messages("mapping.status.backlog.closed")(Lang("en"))

    def open(): String = backlogs.map(_.name).find(_ == OPEN_JA).getOrElse(backlogs.map(_.name).find(_ == OPEN_EN).getOrElse(""))

    def inProgress(): String = backlogs.map(_.name).find(_ == IN_PROGRESS_JA).getOrElse(backlogs.map(_.name).find(_ == IN_PROGRESS_EN).getOrElse(""))

    def resolved(): String = backlogs.map(_.name).find(_ == RESOLVED_JA).getOrElse(backlogs.map(_.name).find(_ == RESOLVED_EN).getOrElse(""))

    def closed(): String = backlogs.map(_.name).find(_ == CLOSED_JA).getOrElse(backlogs.map(_.name).find(_ == CLOSED_EN).getOrElse(""))
  }

  private[this] object Jira {
    val TO_DO_JA: String       = Messages("mapping.status.jira.to_do")(Lang("ja"))
    val IN_PROGRESS_JA: String = Messages("mapping.status.jira.in_progress")(Lang("ja"))
    val IN_REVIEW_JA: String   = Messages("mapping.status.jira.in_review")(Lang("ja"))
    val DONE_JA: String        = Messages("mapping.status.jira.done")(Lang("ja"))
    val TO_DO_EN: String       = Messages("mapping.status.jira.to_do")(Lang("en"))
    val IN_PROGRESS_EN: String = Messages("mapping.status.jira.in_progress")(Lang("en"))
    val IN_REVIEW_EN: String   = Messages("mapping.status.jira.in_review")(Lang("en"))
    val DONE_EN: String        = Messages("mapping.status.jira.done")(Lang("en"))
  }

  override def matchItem(jira: MappingItem): String =
    backlogs.map(_.name).find(_ == jira.name) match {
      case Some(backlog) => backlog
      case None =>
        jira.name match {
          case Jira.TO_DO_JA        | Jira.TO_DO_EN       => Backlog.open()
          case Jira.IN_PROGRESS_JA  | Jira.IN_PROGRESS_EN => Backlog.inProgress()
          case Jira.IN_REVIEW_JA    | Jira.IN_REVIEW_EN   => Backlog.resolved()
          case Jira.DONE_JA         | Jira.DONE_EN        => Backlog.closed()
          case _                                          => ""
        }
    }

  override def jiras: Seq[MappingItem] =
    jiraStatuses.map(status => MappingItem(status.name, status.name))

  override def backlogs: Seq[MappingItem] =
    backlogStatuses.map(status => MappingItem(status.getName, status.getName))

  override def filePath: String = MappingDirectory.STATUS_MAPPING_FILE

  override def itemName: String = Messages("common.statuses")

  override def description: String =
    Messages("cli.mapping.configurable", itemName, backlogs.map(_.name).mkString(","))

  override def isDisplayDetail: Boolean = false
}
