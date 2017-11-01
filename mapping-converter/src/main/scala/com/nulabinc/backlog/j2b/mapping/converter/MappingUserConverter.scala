package com.nulabinc.backlog.j2b.mapping.converter

import com.nulabinc.backlog.j2b.jira.converter.{MappingConverter, UserConverter}
import com.nulabinc.backlog.j2b.jira.domain.Mapping
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain.BacklogUser

class MappingUserConverter(mappings: Seq[Mapping]) extends UserConverter {

  override def convert(user: BacklogUser): BacklogUser = ???
//    user.optUserId match {
//      case Some(userId) =>
//        try {
//          Convert.toBacklog(mappingOfUserId(userId))
//        } catch {
//          case _: Throwable =>
//            Convert.toBacklog(mappingOfName(user.name))
//        }
//      case _ => Convert.toBacklog(mappingOfName(user.name))
//    }

}
