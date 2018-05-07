package com.nulabinc.jira.client.json

import java.text.SimpleDateFormat
import java.util.Date

import spray.json._

object DateTimeMappingJsonProtocol {

  private val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")

  implicit object DateTimeJsonFormat extends RootJsonFormat[Date] {
    def write(datetime: Date): JsValue =
      JsString(datetime.formatted(format.toPattern))

    def read(json: JsValue): Date = json match {
      case JsString(x) => format.parse(x)
      case x           => deserializationError("Expected DateTime as JsString, but got " + x)
    }
  }

}
