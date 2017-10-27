package com.nulabinc.backlog.j2b.mapping.file

import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.modules.{ServiceInjector => JiraInjector}
import com.nulabinc.backlog.j2b.jira.service.{StatusService => JiraStatusService}
import com.nulabinc.backlog.j2b.mapping.core.MappingDirectory
import com.nulabinc.backlog.migration.common.modules.{ServiceInjector => BacklogInjector}
import com.nulabinc.backlog.migration.common.service.{StatusService => BacklogStatusService}
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration
import com.nulabinc.backlog.migration.common.utils.StringUtil
import com.nulabinc.backlog4j.{Status => BacklogStatus}
import com.nulabinc.jira.client.domain.{Status => JiraStatus}
import com.osinka.i18n.{Lang, Messages}

class StatusMappingFile(jiraApiConfig: JiraApiConfiguration,
                        backlogApiConfig: BacklogApiConfiguration,
                        statuses: Seq[String]) extends MappingFile {

  private[this] val jiraItems = getJiraItems()
  private[this] val backlogItems = getBacklogItems()

  private[this] def getJiraItems(): Seq[MappingItem] = {

    val injector      = JiraInjector.createInjector(jiraApiConfig)
    val statusService = injector.getInstance(classOf[JiraStatusService])
    val jiraStatuses  = statusService.allStatuses()

    def createItem(status: JiraStatus): MappingItem = {
      MappingItem(status.name, status.name)
    }

    def condition(target: String)(status: JiraStatus): Boolean = {
      status.id == target
    }

    def collectItems(acc: Seq[MappingItem], status: String): Seq[MappingItem] = {
      if (jiraStatuses.exists(condition(status))) acc
      else acc :+ MappingItem(Messages("cli.mapping.delete_status", status), Messages("cli.mapping.delete_status", status))
    }

    val jiras       = jiraStatuses.map(createItem)
    val deleteItems = statuses.foldLeft(Seq.empty[MappingItem])(collectItems)
    jiras union deleteItems
  }

  private[this] def getBacklogItems(): Seq[MappingItem] = {
    def createItem(status: BacklogStatus): MappingItem = {
      MappingItem(status.getName, status.getName)
    }

    val injector        = BacklogInjector.createInjector(backlogApiConfig)
    val statusService   = injector.getInstance(classOf[BacklogStatusService])
    val backlogStatuses = statusService.allStatuses()
    backlogStatuses.map(createItem)
  }

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
    val NEW_JA: String         = Messages("mapping.status.jira.new")(Lang("ja"))
    val IN_PROGRESS_JA: String = Messages("mapping.status.jira.in_progress")(Lang("ja"))
    val RESOLVED_JA: String    = Messages("mapping.status.jira.resolved")(Lang("ja"))
    val FEEDBACK_JA: String    = Messages("mapping.status.jira.feedback")(Lang("ja"))
    val CLOSED_JA: String      = Messages("mapping.status.jira.closed")(Lang("ja"))
    val REJECTED_JA: String    = Messages("mapping.status.jira.rejected")(Lang("ja"))
    val NEW_EN: String         = Messages("mapping.status.jira.new")(Lang("en"))
    val IN_PROGRESS_EN: String = Messages("mapping.status.jira.in_progress")(Lang("en"))
    val RESOLVED_EN: String    = Messages("mapping.status.jira.resolved")(Lang("en"))
    val FEEDBACK_EN: String    = Messages("mapping.status.jira.feedback")(Lang("en"))
    val CLOSED_EN: String      = Messages("mapping.status.jira.closed")(Lang("en"))
    val REJECTED_EN: String    = Messages("mapping.status.jira.rejected")(Lang("en"))
  }

  override def matchItem(jira: MappingItem): String =
    backlogs.map(_.name).find(_ == jira.name) match {
      case Some(backlog) => backlog
      case None =>
        jira.name match {
          case Jira.NEW_JA | Jira.NEW_EN                 => Backlog.open()
          case Jira.IN_PROGRESS_JA | Jira.IN_PROGRESS_EN => Backlog.inProgress()
          case Jira.RESOLVED_JA | Jira.RESOLVED_EN       => Backlog.resolved()
          case Jira.FEEDBACK_JA | Jira.FEEDBACK_EN       => ""
          case Jira.CLOSED_JA | Jira.CLOSED_EN           => Backlog.closed()
          case Jira.REJECTED_JA | Jira.REJECTED_EN       => ""
          case _                                         => ""
        }
    }

  override def jiras: Seq[MappingItem] = jiraItems

  override def backlogs: Seq[MappingItem] = backlogItems

  override def filePath: String = MappingDirectory.STATUS_MAPPING_FILE

  override def itemName: String = Messages("common.statuses")

  override def description: String =
    Messages("cli.mapping.configurable", itemName, backlogs.map(_.name).mkString(","))

  override def isDisplayDetail: Boolean = false
}
