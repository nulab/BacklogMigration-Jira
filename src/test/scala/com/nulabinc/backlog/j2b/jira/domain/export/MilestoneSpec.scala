package com.nulabinc.backlog.j2b.jira.domain.export

import com.nulabinc.backlog.migration.common.utils.DateUtil
import org.specs2.mutable.Specification

class MilestoneSpec extends Specification {

  "Milestone.from" >> {
    val text1 =
      "com.atlassian.greenhopper.service.sprint.Sprint@7e5681e5[id=10,rapidViewId=2,state=FUTURE,name=goalがないスプリント,goal=,startDate=<null>,endDate=2019-04-13T15:50:00.000Z,completeDate=<null>,sequence=10]"
    val actual1 = Milestone.from(text1)

    actual1.id must equalTo(10)
    actual1.name must equalTo("goalがないスプリント")
    actual1.goal must beNone
    actual1.startDate must beNone
    actual1.endDate must beSome(
      DateUtil.yyyymmddParse("2019-04-13T15:50:00.000Z")
    )

    // BLGMIGRATION-813
    val text2 =
      "com.atlassian.greenhopper.service.sprint.Sprint@2e5wdc1e5[rapidViewId=1,state=CLOSED,name=Sixth Sprint,startDate=2012-11-20T13:14:42.960-08:00,endDate=2012-12-03T13:14:00.000-08:00,goal=test goal,completeDate=2015-10-07T08:55:24.294-07:00,sequence=22,id=22]"
    val actual2 = Milestone.from(text2)

    actual2.id must equalTo(22)
    actual2.name must equalTo("Sixth Sprint")
    actual2.goal must beSome("test goal")
    actual2.startDate must beSome("2012-11-20T13:14:42.960-08:00")
    actual2.endDate must beSome(
      DateUtil.yyyymmddParse("2012-12-03T05:14:00.000Z")
    )

    // BLGMIGRATION-908
    val text3 =
      """{"boardId":10,"endDate":"2020-10-01T04:04:00.000Z","goal":"future","id":122,"name":"スプリント 3","startDate":"2020-09-18T04:04:24.041Z","state":"active"}"""
    val actual3 = Milestone.from(text3)

    actual3.id must equalTo(122)
    actual3.name must equalTo("スプリント 3")
    actual3.goal must beSome("future")
    actual3.startDate must beSome("2020-09-18T04:04:24.041Z")
    actual3.endDate must beSome(
      DateUtil.yyyymmddParse("2020-10-01T04:04:00.000Z")
    )
  }

}
