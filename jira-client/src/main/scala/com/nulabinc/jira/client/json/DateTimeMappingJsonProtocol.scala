package com.nulabinc.jira.client.json

import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.ISODateTimeFormat
import spray.json._

object DateTimeMappingJsonProtocol {

  implicit object DateTimeJsonFormat extends RootJsonFormat[DateTime] {
    private lazy val format = ISODateTimeFormat.dateTimeNoMillis()
    def write(datetime: DateTime): JsValue = JsString(format.print(datetime.withZone(DateTimeZone.UTC)))
    def read(json: JsValue): DateTime = json match {
      case JsString(x) => format.parseDateTime(x)
      case x           => deserializationError("Expected DateTime as JsString, but got " + x)
    }
  }

}
