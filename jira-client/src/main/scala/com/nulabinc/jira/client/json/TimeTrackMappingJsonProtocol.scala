package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.issue.TimeTrack
import spray.json._

object TimeTrackMappingJsonProtocol extends DefaultJsonProtocol {

  implicit object FieldSchemaMappingFormat extends RootJsonFormat[TimeTrack] {
    def write(obj: TimeTrack) = ???

    def read(json: JsValue) = {
      val obj = json.asJsObject
      val originalEstimateSeconds =
        obj.getFields("originalEstimateSeconds") match {
          case Seq(JsNumber(sec)) => Some(sec.toInt)
          case _                  => None
        }
      val timeSpentSeconds = obj.getFields("timeSpentSeconds") match {
        case Seq(JsNumber(sec)) => Some(sec.toInt)
        case _                  => None
      }
      TimeTrack(
        originalEstimateSeconds = originalEstimateSeconds,
        timeSpentSeconds = timeSpentSeconds
      )
    }
  }
}
