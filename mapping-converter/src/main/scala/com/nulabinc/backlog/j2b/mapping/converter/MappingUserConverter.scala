package com.nulabinc.backlog.j2b.mapping.converter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.converter.UserConverter
import com.nulabinc.backlog.j2b.jira.domain.mapping.{Mapping, MappingCollectDatabase}
import com.nulabinc.backlog.j2b.mapping.converter.writes.UserWrites
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain.BacklogUser
import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, Logging}
import com.osinka.i18n.Messages

class MappingUserConverter @Inject()(implicit val userWrites: UserWrites)
    extends UserConverter with Logging {

  override def convert(mappingCollectDatabase: MappingCollectDatabase, mappings: Seq[Mapping], user: BacklogUser): BacklogUser =
    user.optUserId match {
      case Some(userId) =>
        try {
          Convert.toBacklog(mappingOfUserId(mappings, userId))
        } catch {
          case _: Throwable =>
            Convert.toBacklog(mappingOfName(mappingCollectDatabase, mappings, user.name))
        }
      case _ =>
        val m = mappingOfName(mappingCollectDatabase, mappings, user.name)
        Convert.toBacklog(m)
    }

  override def convert(mappingCollectDatabase: MappingCollectDatabase, mappings: Seq[Mapping], user: String) =
    mappingOfName(mappingCollectDatabase, mappings, user).dst

  private def mappingOfUserId(mappings: Seq[Mapping], userId: String): Mapping =
    mappings.find(_.dst.trim == userId.trim) match {
      case Some(mapping) if mapping.dst.nonEmpty => Mapping(None, userId, userId)
      case _ => mappings.find( u => u.src.trim == userId.trim) match {
        case Some(mapping) if mapping.dst.nonEmpty => mapping
        case _ =>
          ConsoleOut.error(Messages("convert.user.failed", userId))
          throw new RuntimeException(Messages("convert.user.failed", userId))
      }
    }

  private def mappingOfName(mappingCollectDatabase: MappingCollectDatabase, mappings: Seq[Mapping], userName: String): Mapping =
    mappings.find(u => u.dst.trim == userName.trim) match {
      case Some(mapping) if mapping.dst.nonEmpty => Mapping(None, mapping.dst, mapping.dst)
      case _ => mappings.find(u => u.src.trim == userName.trim) match {
        case Some(mapping) if mapping.dst.nonEmpty => Mapping(None, mapping.dst, mapping.dst)
        case _                                     => mappingOfInfoName(mappingCollectDatabase, mappings, userName)
      }
    }

  private def mappingOfInfoName(mappingCollectDatabase: MappingCollectDatabase, mappings: Seq[Mapping], userName: String): Mapping = {
    mappingCollectDatabase.findByName(Some(userName)) match {
      case Some(user) => Mapping(None, user.name, user.name)
      case _          => Mapping(None, userName, userName)
    }
  }
}
