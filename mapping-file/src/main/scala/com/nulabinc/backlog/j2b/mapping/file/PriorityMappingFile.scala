package com.nulabinc.backlog.j2b.mapping.file

import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration
import com.nulabinc.backlog.migration.common.modules.{ServiceInjector => BacklogInjector}
import com.nulabinc.backlog.migration.common.service.{PriorityService => BacklogPriorityService}
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.jira.domain.mapping.{MappingFile, MappingItem}
import com.nulabinc.backlog.j2b.mapping.core.MappingDirectory
import com.nulabinc.jira.client.domain.{Priority => JiraPriority}
import com.nulabinc.backlog4j.{Priority => BacklogPriority}
import com.osinka.i18n.{Lang, Messages}

class PriorityMappingFile(jiraApiConfig: JiraApiConfiguration,
                          backlogApiConfig: BacklogApiConfiguration,
                          priorities: Seq[JiraPriority]) extends MappingFile {

  private[this] val jiraItems = getJiraItems()
  private[this] val backlogItems = getBacklogItems()

  private[this] def getJiraItems(): Seq[MappingItem] = {
    def createItem(priority: JiraPriority): MappingItem = {
      MappingItem(priority.name, priority.name)
    }
    priorities.map(createItem)
  }

  private[this] def getBacklogItems(): Seq[MappingItem] = {
    def createItem(priority: BacklogPriority): MappingItem = {
      MappingItem(priority.getName, priority.getName)
    }
    val injector          = BacklogInjector.createInjector(backlogApiConfig)
    val priorityService   = injector.getInstance(classOf[BacklogPriorityService])
    val backlogPriorities = priorityService.allPriorities()
    backlogPriorities.map(createItem)
  }

  private object Backlog {
    val LOW_JA: String    = Messages("mapping.priority.backlog.low")(Lang("ja"))
    val NORMAL_JA: String = Messages("mapping.priority.backlog.normal")(Lang("ja"))
    val HIGH_JA: String   = Messages("mapping.priority.backlog.high")(Lang("ja"))
    val LOW_EN: String    = Messages("mapping.priority.backlog.low")(Lang("en"))
    val NORMAL_EN: String = Messages("mapping.priority.backlog.normal")(Lang("en"))
    val HIGH_EN: String   = Messages("mapping.priority.backlog.high")(Lang("en"))

    def low(): String = backlogs.map(_.name).find(_ == LOW_JA).getOrElse(backlogs.map(_.name).find(_ == LOW_EN).getOrElse(""))

    def normal(): String = backlogs.map(_.name).find(_ == NORMAL_JA).getOrElse(backlogs.map(_.name).find(_ == NORMAL_EN).getOrElse(""))

    def high(): String = backlogs.map(_.name).find(_ == HIGH_JA).getOrElse(backlogs.map(_.name).find(_ == HIGH_EN).getOrElse(""))
  }

  private object Jira {
    val LOWEST_JA: String    = Messages("mapping.priority.jira.lowest")(Lang("ja"))
    val LOW_JA: String       = Messages("mapping.priority.jira.low")(Lang("ja"))
    val MEDIUM_JA: String    = Messages("mapping.priority.jira.medium")(Lang("ja"))
    val HIGH_JA: String      = Messages("mapping.priority.jira.high")(Lang("ja"))
    val HIGHEST_JA: String   = Messages("mapping.priority.jira.highest")(Lang("ja"))
    val LOWEST_EN: String    = Messages("mapping.priority.jira.lowest")(Lang("en"))
    val LOW_EN: String       = Messages("mapping.priority.jira.low")(Lang("en"))
    val MEDIUM_EN: String    = Messages("mapping.priority.jira.medium")(Lang("en"))
    val HIGH_EN: String      = Messages("mapping.priority.jira.high")(Lang("en"))
    val HIGHEST_EN: String   = Messages("mapping.priority.jira.highest")(Lang("en"))
  }

  override def matchItem(jira: MappingItem): String =
    backlogs.map(_.name).find(_ == jira.name) match {
      case Some(backlog) => backlog
      case None =>
        jira.name match {
          case Jira.LOWEST_JA  | Jira.LOWEST_EN  => Backlog.low()
          case Jira.LOW_JA     | Jira.LOW_EN     => Backlog.low()
          case Jira.MEDIUM_JA  | Jira.MEDIUM_EN  => Backlog.normal()
          case Jira.HIGH_JA    | Jira.HIGH_EN    => Backlog.high()
          case Jira.HIGHEST_JA | Jira.HIGHEST_EN => Backlog.high()
          case _                                 => ""
        }
    }

  override def jiras: Seq[MappingItem] = jiraItems

  override def backlogs: Seq[MappingItem] = backlogItems

  override def filePath: String = MappingDirectory.PRIORITY_MAPPING_FILE

  override def itemName: String = Messages("common.priorities")

  override def description: String =
    Messages("cli.mapping.configurable", itemName, backlogs.map(_.name).mkString(","))

  override def isDisplayDetail: Boolean = false
}
