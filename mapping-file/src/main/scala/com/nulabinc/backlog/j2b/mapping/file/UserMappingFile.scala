package com.nulabinc.backlog.j2b.mapping.file

import com.nulabinc.backlog.migration.common.conf.{BacklogApiConfiguration, BacklogConfiguration}
import com.nulabinc.backlog.migration.common.domain.BacklogUser
import com.nulabinc.backlog.migration.common.modules.{ServiceInjector => BacklogInjector}
import com.nulabinc.backlog.migration.common.service.{UserService => BacklogUserService}
import com.nulabinc.backlog.migration.common.utils.StringUtil
import com.nulabinc.backlog.j2b.mapping.core.MappingDirectory
import com.nulabinc.backlog.j2b.mapping.domain.MappingJsonProtocol._
import com.nulabinc.backlog.j2b.mapping.domain.{Mapping, MappingsWrapper}
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.jira.client.domain.{User => JiraUser}
import com.osinka.i18n.Messages
import spray.json.JsonParser
import scalax.file.Path

class UserMappingFile(jiraApiConfig: JiraApiConfiguration,
                      backlogApiConfig: BacklogApiConfiguration,
                      users: Seq[JiraUser]) extends MappingFile with BacklogConfiguration {

  private[this] val jiraItems = getJiraItems()
  private[this] val backlogItems = getBacklogItems()

  private[this] def getJiraItems(): Seq[MappingItem] = {

    def resolve(user: JiraUser): Option[JiraUser] = {
      (Option(user.name), Option(user.displayName)) match {
        case (Some(_), Some(_)) => Some(user)
        case _                  => None
      }
    }

    def condition(user: JiraUser): Boolean = {
      StringUtil.notEmpty(user.name).nonEmpty
    }

    def createItem(user: JiraUser): MappingItem = {
      MappingItem(user.name, user.displayName)
    }

    users.flatMap(resolve).filter(condition).map(createItem)
  }

  private[this] def getBacklogItems(): Seq[MappingItem] = {
    def createItem(user: BacklogUser): MappingItem = {
      if (backlogApiConfig.url.contains(NaiSpaceDomain)) {
        MappingItem(user.optMailAddress.getOrElse(""), user.name)
      } else {
        MappingItem(user.optUserId.getOrElse(""), user.name)
      }
    }
    val backlogUsers = allUsers()
    backlogUsers.map(createItem)
  }

  private[this] def allUsers(): Seq[BacklogUser] = {
    val injector    = BacklogInjector.createInjector(backlogApiConfig)
    val userService = injector.getInstance(classOf[BacklogUserService])
    userService.allUsers()
  }

  private[this] def convertForNAI(backlogUsers: Seq[BacklogUser])(mapping: Mapping) = {
    if (backlogApiConfig.url.contains(NaiSpaceDomain)) {
      val targetBacklogUser = backlogUsers
        .find(backlogUser => backlogUser.optMailAddress.getOrElse("") == mapping.backlog)
        .getOrElse(throw new NoSuchElementException(s"User ${mapping.backlog} not found"))
      mapping.copy(backlog = targetBacklogUser.optUserId.getOrElse(s"UserId ${mapping.backlog} not found"))
    } else mapping
  }

  override def tryUnmarshal(): Seq[Mapping] = {
    val path    = Path.fromString(filePath)
    val json    = path.lines().mkString
    val convert = convertForNAI(allUsers()) _
    JsonParser(json).convertTo[MappingsWrapper].mappings.map(convert)
  }

  override def matchItem(jira: MappingItem): String =
    backlogs.map(_.name).find(_ == jira.name).getOrElse("")

  override def jiras: Seq[MappingItem] = jiraItems

  override def backlogs: Seq[MappingItem] = backlogItems

  override def filePath: String = MappingDirectory.USER_MAPPING_FILE

  override def itemName: String = Messages("common.users")

  override def description: String =
    Messages("cli.mapping.configurable", itemName, backlogs.map(_.name).mkString(","))

  override def isDisplayDetail: Boolean = true

}
