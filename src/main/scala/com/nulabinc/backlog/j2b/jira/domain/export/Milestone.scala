package com.nulabinc.backlog.j2b.jira.domain.export

import java.util.Date

import com.nulabinc.backlog.migration.common.utils.DateUtil
import spray.json._
import spray.json.DefaultJsonProtocol
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

  implicit class ResultOps[A](result: Either[MilestoneError, A]) {
    def orError(input: String): A =
      result match {
        case Right(value) =>
          value
        case Left(error) =>
          val message = error match {
            case err: ExtractError =>
              s"'=' not found. Raw input: ${err.rawInput}"
            case IdNotFound   => s"Id not found"
            case NameNotFound => s"Name not found"
          }
          throw new RuntimeException(
            s"Unable to parse milestone. Error: $message Raw input: $input"
          )
      }
  }

  implicit object milestoneReader extends JsonReader[Milestone] {
    private def getOptString(obj: JsObject, fieldName: String): Option[String] =
      obj.getFields(fieldName) match {
        case Seq(JsString(value)) => Some(value)
        case _                    => None
      }

    override def read(json: JsValue): Milestone = {
      val obj = json.asJsObject
      val (id, name) = obj.getFields("id", "name") match {
        case Seq(JsNumber(id), JsString(name)) =>
          (id, name)
        case _ =>
          deserializationError(
            s"Cannot deserialize milestone of must fields. Input: ${json.prettyPrint}"
          )
      }

      Milestone(
        id = id.toLong,
        name = name,
        goal = getOptString(obj, "goal"),
        startDate = getOptString(obj, "startDate"),
        endDate = getOptString(obj, "endDate").map(DateUtil.yyyymmddParse)
      )
    }
  }

  sealed trait MilestoneError
  case class ExtractError(rawInput: String) extends MilestoneError
  case object IdNotFound                    extends MilestoneError
  case object NameNotFound                  extends MilestoneError

  type MileStoneParams = Map[String, String]

  val pattern: Regex = """.*?\[(.+?)]$""".r

  def from(text: String): Milestone =
    pattern.findFirstMatchIn(text) match {
      case Some(m) =>
        val result = for {
          params <- split(m.group(1)).map(_.trim).map(extract).sequence.map(_.toMap[String, String])
          milestone <- for {
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
        } yield milestone
        result.orError(text)
      case _ =>
        text.parseJson.convertTo[Milestone]
    }

  def from(fieldDefinitions: Seq[Field], issueFields: Seq[IssueField]): Seq[Milestone] =
    fieldDefinitions
      .find(_.name == "Sprint")
      .map { sprintDefinition =>
        issueFields.find(_.id == sprintDefinition.id) match {
          case Some(IssueField(_, ArrayFieldValue(values))) =>
            values.map(v => Milestone.from(v.value))
          case _ => Seq()
        }
      }
      .getOrElse(Seq())

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
