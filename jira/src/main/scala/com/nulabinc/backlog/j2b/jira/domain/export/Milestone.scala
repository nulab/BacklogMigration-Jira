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

// com.atlassian.greenhopper.service.sprint.Sprint@2e84f4e0[id=4,rapidViewId=2,state=FUTURE,name=default スプリント 2,goal=<null>,startDate=<null>,endDate=<null>,completeDate=<null>,sequence=4]

object Milestone {

  val pattern: Regex = """id=(\d+),.*?name=(.+?),.*?goal=(.+?),.*?startDate=(.+?),endDate=(.+?),""".r

  def apply(text: String): Milestone = {

    pattern.findFirstMatchIn(text) match {
      case Some(m) => new Milestone(
        id = m.group(1).toLong,
        name = m.group(2),
        goal = m.group(3) match {
          case "<null>" => None
          case string   => Some(string)
        },
        startDate = m.group(4) match {
          case "<null>" => None
          case string   => Some(string)
        },
        endDate = m.group(5) match {
          case "<null>" => None
          case string   => Some(DateUtil.yyyymmddParse(string))
        }
      )
      case None => throw new RuntimeException("Cannot parse milestone. input: " + text)
    }
  }
}
