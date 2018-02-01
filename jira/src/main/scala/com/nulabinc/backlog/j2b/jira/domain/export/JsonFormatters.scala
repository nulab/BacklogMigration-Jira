package com.nulabinc.backlog.j2b.jira.domain.export

import com.nulabinc.jira.client.domain.User
import spray.json._

import scala.util.{Success, Try}

object JsonFormatters extends DefaultJsonProtocol {

    import com.nulabinc.jira.client.json.UserMappingJsonProtocol._

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

      def write(a: FieldValue) = ???

      def read(value: JsValue) = convertToIssueFieldValue(value)

      private [this] def convertToIssueFieldValue(value: JsValue): FieldValue =
        value match {
          case v: JsNumber => NumberFieldValue(v.value)
          case v: JsArray  => ArrayFieldValue(v.elements.map(convertToIssueFieldValue))
          case v: JsObject =>
            List(
              Try { OptionFieldValue(v.convertTo[IssueFieldOption]).asInstanceOf[FieldValue] },
              Try { UserFieldValue(v.convertTo[User]).asInstanceOf[FieldValue] },
              Try { StringFieldValue(v.toString) }
            ).filter(_.isSuccess).head.getOrElse(StringFieldValue(v.convertTo[String]))
          case v: JsString => StringFieldValue(v.value.replace("\"", ""))
          case v           => StringFieldValue(v.toString)
        }
    }

    implicit val stringFormat = jsonFormat1(StringFieldValue)
    implicit val numberFormat = jsonFormat1(NumberFieldValue)
    implicit val arrayFormat  = jsonFormat1(ArrayFieldValue)
    implicit val optionFormat = jsonFormat1(OptionFieldValue)
    implicit val userFormat   = jsonFormat1(UserFieldValue)

}
