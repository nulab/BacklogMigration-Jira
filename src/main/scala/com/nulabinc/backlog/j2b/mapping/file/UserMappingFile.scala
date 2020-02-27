package com.nulabinc.backlog.j2b.mapping.file

import better.files.File
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingJsonProtocol._
import com.nulabinc.backlog.j2b.jira.domain.mapping.{Mapping, MappingFile, MappingItem, MappingsWrapper}
import com.nulabinc.backlog.j2b.mapping.core.MappingDirectory
import com.nulabinc.backlog.migration.common.conf.{BacklogApiConfiguration, BacklogConfiguration}
import com.nulabinc.backlog.migration.common.domain.BacklogUser
import com.nulabinc.backlog.migration.common.utils.{IOUtil, StringUtil}
import com.nulabinc.jira.client.domain.{User => JiraUser}
import com.osinka.i18n.Messages
import spray.json.JsonParser

class UserMappingFile(backlogApiConfig: BacklogApiConfiguration,
                      jiraUsers: Seq[JiraUser],
                      backlogUsers: Seq[BacklogUser]) extends MappingFile with BacklogConfiguration {


  private[this] def convertForNAI(backlogUsers: Seq[BacklogUser])(mapping: Mapping) = {
    if (backlogApiConfig.url.contains(NaiSpaceDomain)) {
      val targetBacklogUser = backlogUsers
        .find(backlogUser => backlogUser.optMailAddress.getOrElse("") == mapping.dst)
        .getOrElse(throw new NoSuchElementException(s"User ${mapping.dst} not found"))
      mapping.copy(dst = targetBacklogUser.optUserId.getOrElse(s"UserId ${mapping.dst} not found"))
    } else mapping
  }

  override def tryUnMarshal(): Seq[Mapping] = {
    val path = File(filePath).path.toAbsolutePath
    val json = IOUtil.input(path).getOrElse("")
    val convert = convertForNAI(backlogUsers) _
    JsonParser(json).convertTo[MappingsWrapper].mappings.map(convert)
  }

  override def matchItem(jira: MappingItem): String =
    backlogs.map(_.name).find(_ == jira.name).getOrElse("")

  override def jiras: Seq[MappingItem] = {

    def resolve(user: JiraUser): Option[JiraUser] = {
      (Option(user.name), Option(user.displayName)) match {
        case (Some(_), Some(_)) => Some(user)
        case _                  => None
      }
    }

    def condition(user: JiraUser): Boolean = {
      StringUtil.notEmpty(user.emailAddress.getOrElse("")).nonEmpty
    }

    def createItem(user: JiraUser): MappingItem = {
      MappingItem(user.name.getOrElse(""), user.displayName)
    }

    jiraUsers.flatMap(resolve).filter(condition).map(createItem)
  }

  override def backlogs: Seq[MappingItem] = {
    def createItem(user: BacklogUser): MappingItem = {
      if (backlogApiConfig.url.contains(NaiSpaceDomain)) {
        MappingItem(user.optMailAddress.getOrElse(""), user.name)
      } else {
        MappingItem(user.optUserId.getOrElse(""), user.name)
      }
    }
    backlogUsers.map(createItem)
  }

  override def filePath: String = MappingDirectory.USER_MAPPING_FILE

  override def itemName: String = Messages("common.users")

  override def description: String =
    Messages("cli.mapping.configurable", itemName, backlogs.map(_.name).mkString(","))

  override def isDisplayDetail: Boolean = true

}

