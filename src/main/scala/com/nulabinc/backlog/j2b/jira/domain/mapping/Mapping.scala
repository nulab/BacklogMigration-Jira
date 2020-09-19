package com.nulabinc.backlog.j2b.jira.domain.mapping

import com.nulabinc.backlog.migration.common.domain.BacklogUser
import com.nulabinc.backlog.migration.common.utils.Logging
import spray.json.DefaultJsonProtocol

case class MappingsWrapper(`//`: String, mappings: Seq[Mapping])

case class MappingInfo(name: String, mail: String)

case class Mapping(info: Option[MappingInfo], src: String, dst: String)

object Mapping extends Logging {

  def create(user: BacklogUser) =
    user.optUserId match {
      case Some(userId) =>
        Mapping(
          info = Some(
            MappingInfo(
              name = user.name,
              mail = user.optMailAddress.getOrElse("")
            )
          ),
          src = userId,
          dst = ""
        )
      case None =>
        Mapping(
          info = Some(
            MappingInfo(
              name = user.name,
              mail = user.optMailAddress.getOrElse("")
            )
          ),
          src = user.name,
          dst = ""
        )
    }

  def create(value: String) =
    Mapping(
      info = Some(MappingInfo(name = value, mail = "")),
      src = value,
      dst = ""
    )

}

object MappingJsonProtocol extends DefaultJsonProtocol {
  implicit val MappingDescriptionFormat = jsonFormat2(MappingInfo)
  implicit val MappingFormat            = jsonFormat3(Mapping.apply)
  implicit val MappingsWrapperFormat    = jsonFormat2(MappingsWrapper)
}

sealed abstract class MappingType(val value: String) {
  def intValue(): Int
}

object MappingType {

  case object UserId extends MappingType("UserId") {
    override def toString: String = value

    override def intValue(): Int = 0
  }

  case object Name extends MappingType("Name") {
    override def toString: String = value

    override def intValue(): Int = 1
  }

  val all = Seq(UserId, Name)

  def apply(value: String): MappingType = {
    value match {
      case "UserId" => UserId
      case "Name"   => Name
      case _        => throw new IllegalArgumentException(s"Illegal value ($value)")
    }
  }
}
