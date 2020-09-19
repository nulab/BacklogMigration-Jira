package com.nulabinc.backlog.j2b.jira.domain.export

import java.util.Date

import com.nulabinc.backlog.migration.common.utils.DateUtil

import scala.util.matching.Regex

case class Milestone(
    id: Long,
    name: String,
    goal: Option[String],
    startDate: Option[String],
    endDate: Option[Date]
)

object Milestone {

  implicit class SeqEitherOps[A, E](results: Seq[Either[E, A]]) {
    def sequence: Either[E, Seq[A]] =
      results.foldLeft(Right(Seq.empty[A]): Either[E, Seq[A]]) {
        case (acc, Left(_))     => acc
        case (acc, Right(item)) => acc.map(_ :+ item)
      }
  }

  sealed trait MilestoneError
  case class ExtractError(rawInput: String) extends MilestoneError
  case object IdNotFound                    extends MilestoneError
  case object NameNotFound                  extends MilestoneError

  type MileStoneParams = Map[String, String]

  val pattern: Regex = """.*?\[(.+?)]$""".r

  def apply(text: String): Milestone = {

    val value = pattern.findFirstMatchIn(text) match {
      case Some(m) => m.group(1)
      case _ =>
        throw new RuntimeException(
          "Unable to parse milestone. Raw input: " + text
        )
    }

    split(value)
      .map(_.trim)
      .map(extract)
      .sequence
      .map(_.toMap[String, String])
      .flatMap { params =>
        for {
          id   <- findId(params)
          name <- findName(params)
        } yield {
          new Milestone(
            id = id.toLong,
            name = name,
            goal = findValue(params, "goal"),
            startDate = findValue(params, "startDate"),
            endDate = findValue(params, "endDate").map(DateUtil.yyyymmddParse)
          )
        }
      }
      .fold(
        error => {
          val message = error match {
            case err: ExtractError =>
              s"'=' not found. Raw input: ${err.rawInput}"
            case IdNotFound   => s"Id not found"
            case NameNotFound => s"Name not found"
          }
          throw new RuntimeException(
            s"Unable to parse milestone. Error: $message Raw input: $text"
          )
        },
        value => value
      )
  }

  private def split(str: String): Seq[String] =
    str.split(",")

  private def extract(str: String): Either[MilestoneError, (String, String)] = {
    val arr = str.split("=")
    if (arr.length < 2) {
      Left(ExtractError(str))
    } else {
      Right(arr(0) -> arr(1))
    }
  }

  private def findValue(params: MileStoneParams, key: String): Option[String] =
    params.get(key).flatMap {
      case "<null>"           => None
      case str if str.isEmpty => None
      case str                => Some(str)
    }

  private def mustFind[E <: MilestoneError](
      params: MileStoneParams,
      key: String,
      error: String => E
  ): Either[E, String] =
    params.get(key).map(Right(_)).getOrElse(Left(error(key)))

  private def findId(params: MileStoneParams): Either[MilestoneError, String] =
    mustFind(params, "id", _ => IdNotFound)

  private def findName(
      params: MileStoneParams
  ): Either[MilestoneError, String] =
    mustFind(params, "name", _ => NameNotFound)
}
