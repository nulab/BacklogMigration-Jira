package com.nulabinc.backlog.j2b.jira.domain.export

import com.nulabinc.backlog.migration.common.utils.DateUtil
import org.specs2.mutable.Specification

class MilestoneSpec extends Specification {

  "Milestone.apply" >> {
    val text1 = "id=10,rapidViewId=2,state=FUTURE,name=goalがないスプリント,goal=,startDate=<null>,endDate=2019-04-13T15:50:00.000Z,completeDate=<null>,sequence=10"
    val actual1 = Milestone(text1)

    actual1.id must equalTo(10)
    actual1.name must equalTo("goalがないスプリント")
    actual1.goal must beNone
    actual1.startDate must beNone
    actual1.endDate must beSome(DateUtil.yyyymmddParse("2019-04-13T15:50:00.000Z"))

    val text2 = "rapidViewId=3,state=CLOSED,name=Sixth Sprint,startDate=2015-01-20T13:14:42.960-08:00,endDate=2015-02-03T13:14:00.000-08:00,completeDate=2015-10-07T08:55:24.294-07:00,sequence=13,id=13"
    val actual2 = Milestone(text2)

    actual2.id must equalTo(10)
    actual2.name must equalTo("Sixth Sprint")
    actual2.goal must beNone
    actual2.startDate must beSome("2015-01-20T13:14:42.960-08:00")
    actual2.endDate must beSome(DateUtil.yyyymmddParse("2019-04-13T15:50:00.000Z"))
  }

}
