package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.issue._
import com.nulabinc.jira.client.json.IssueMappingJsonProtocol.jsonFormat1
import spray.json._

import scala.util.Try

object IssueFieldMappingJsonProtocol extends DefaultJsonProtocol {

  import UserMappingJsonProtocol._

  implicit object IssueFieldOptionMappingFormat extends RootJsonFormat[IssueFieldOption] {
    def write(obj: IssueFieldOption) = JsObject(
      "id" -> JsString(obj.id.toString),
      "value" -> obj.value.toJson
    )

    def read(json: JsValue) = {
      val jsObject = json.asJsObject
      jsObject.getFields("id", "value") match {
        case Seq(JsString(id), value) => IssueFieldOption(id.toLong, value.convertTo[FieldValue])
        case other => deserializationError("Cannot deserialize IssueFieldOption: invalid input. Raw input: " + other)
      }
    }
  }

  implicit object IssueFieldValueJsonFormat extends RootJsonFormat[FieldValue] {
    def write(a: FieldValue) = a match {
      case v: StringFieldValue => v.toJson
      case v: NumberFieldValue => v.toJson
      case v: ArrayFieldValue  => v.toJson
      case v: OptionFieldValue => v.toJson
      case v: AnyFieldValue    => v.toJson
      case v: UserFieldValue   => v.toJson
    }
    def read(value: JsValue) = convertToIssueFieldValue(value)

    private [this] def convertToIssueFieldValue(value: JsValue): FieldValue =
      value match {
        case v: JsNumber => NumberFieldValue(v.value)
        case v: JsArray  => ArrayFieldValue(v.elements.map(convertToIssueFieldValue))
        case v: JsObject => {
          List(
            Try { OptionFieldValue(v.convertTo[IssueFieldOption]).asInstanceOf[FieldValue] },
            Try { UserFieldValue(v.convertTo[User]).asInstanceOf[FieldValue] }
          ).filter(_.isSuccess).head.getOrElse(AnyFieldValue(v.toString))
        }
        case v => StringFieldValue(v.toString)
      }
  }

  implicit val stringFormat = jsonFormat1(StringFieldValue)
  implicit val numberFormat = jsonFormat1(NumberFieldValue)
  implicit val arrayFormat  = jsonFormat1(ArrayFieldValue)
  implicit val optionFormat = jsonFormat1(OptionFieldValue)
  implicit val anyFormat    = jsonFormat1(AnyFieldValue)
  implicit val userFormat   = jsonFormat1(UserFieldValue)
}
