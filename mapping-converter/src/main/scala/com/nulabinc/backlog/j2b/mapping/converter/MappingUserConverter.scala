package com.nulabinc.backlog.j2b.mapping.converter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.converter.UserConverter
import com.nulabinc.backlog.j2b.jira.domain.{Mapping, MappingType}
import com.nulabinc.backlog.j2b.mapping.converter.writes.UserWrites
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain.BacklogUser
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages

class MappingUserConverter @Inject()(implicit val userWrites: UserWrites)
    extends UserConverter with Logging {

  override def convert(mappings: Seq[Mapping], user: BacklogUser): BacklogUser =
    user.optUserId match {
      case Some(userId) =>
        try {
          Convert.toBacklog(mappingOfUserId(mappings, userId))
        } catch {
          case _: Throwable =>
            Convert.toBacklog(mappingOfName(mappings, user.name))
        }
      case _ => Convert.toBacklog(mappingOfName(mappings, user.name))
    }

  override def convert(mappings: Seq[Mapping], user: String) =
    mappingOfName(mappings, user).dst

  private def mappingOfUserId(mappings: Seq[Mapping], userId: String): Mapping = {
    mappings.filter(_.getMappingType() == MappingType.UserId).find(_.src.trim == userId.trim) match {
      case Some(mapping) if mapping.dst.nonEmpty => mapping
      case _ =>
        ConsoleOut.error(Messages("convert.user.failed", userId))
        throw new RuntimeException(Messages("convert.user.failed", userId))
    }
  }

  private def mappingOfName(mappings: Seq[Mapping], userName: String): Mapping = {
    mappings.find(_.src.trim == userName.trim) match {
      case Some(mapping) if mapping.dst.nonEmpty => mapping
      case _                                     => mappingOfInfoName(mappings, userName)
    }
  }

  private def mappingOfInfoName(mappings: Seq[Mapping], userName: String): Mapping = {
    mappings.find(_.info.map(_.name).getOrElse("").trim == userName.trim) match {
      case Some(mapping) if mapping.dst.nonEmpty => mapping
      case _                                     => mappings.find(_.dst.trim == userName.trim) match {
        case Some(user) => user
        case _          => {
          ConsoleOut.error(Messages("convert.user.failed", userName))
          throw new RuntimeException(Messages("convert.user.failed", userName))
        }
      }

    }
  }
}
