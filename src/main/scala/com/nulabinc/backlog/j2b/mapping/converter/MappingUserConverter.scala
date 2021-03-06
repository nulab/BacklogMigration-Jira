package com.nulabinc.backlog.j2b.mapping.converter

import com.nulabinc.backlog.j2b.jira.domain.mapping.ValidatedJiraUserMapping
import com.nulabinc.backlog.j2b.mapping.converter.writes.UserWrites
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain.BacklogUser
import com.nulabinc.backlog.migration.common.domain.mappings.BacklogUserMappingItem
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages

object MappingUserConverter extends Logging {

  private implicit val userWrites: UserWrites = new UserWrites()

  def convert(
      mappings: Seq[ValidatedJiraUserMapping],
      user: BacklogUser
  ): BacklogUser =
    user.optUserId match {
      case Some(userId) =>
        Convert.toBacklog(mappingOfAccountId(mappings, userId))
      case _ =>
//        val m = mappingOfName(mappingCollectDatabase, mappings, user.name)
//        Convert.toBacklog(m)
        ConsoleOut.error(Messages("convert.user.failed", user.name))
        throw new RuntimeException(Messages("convert.user.failed", user.name))
    }

  def convert(mappings: Seq[ValidatedJiraUserMapping], user: String): String =
    mappingOfAccountId(mappings, user).dst.value

  private def mappingOfAccountId(
      mappings: Seq[ValidatedJiraUserMapping],
      accountId: String
  ): ValidatedJiraUserMapping =
    mappings.find(_.dst.value == accountId.trim) match {
      case Some(mapping) =>
        ValidatedJiraUserMapping(mapping.src, BacklogUserMappingItem(accountId))
      case _ =>
        mappings.find(_.src.accountId == accountId.trim) match {
          case Some(mapping) =>
            mapping
          case _ =>
            ConsoleOut.error(Messages("convert.user.failed", accountId))
            throw new RuntimeException(
              Messages("convert.user.failed", accountId)
            )
        }
    }

//  private def mappingOfName(mappingCollectDatabase: MappingCollectDatabase, mappings: Seq[ValidatedJiraUserMapping], userName: String): ValidatedJiraUserMapping =
//    mappings.find(_.dst.value == userName.trim) match {
//      case Some(mapping) =>
//        mapping
//      case _ =>
//        mappings.find(_.src.displayName == userName.trim).getOrElse(mappingOfInfoName(mappingCollectDatabase, userName))
//    }

//  private def mappingOfInfoName(mappingCollectDatabase: MappingCollectDatabase, userName: String): ValidatedJiraUserMapping = {
//    mappingCollectDatabase.findUser(userName) match {
//      case Some(user) => ValidatedJiraUserMapping(JiraUserMappingItem(user.key, user.displayName), user., user.displayName, user.displayName)
//      case _          => ValidatedJiraUserMapping(None, userName, userName)
//    }
//  }
}
