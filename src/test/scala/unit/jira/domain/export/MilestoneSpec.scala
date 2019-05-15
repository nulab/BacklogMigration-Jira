package unit.jira.domain.export

import com.nulabinc.backlog.j2b.jira.domain.export.Milestone
import com.nulabinc.backlog.migration.common.utils.DateUtil
import org.specs2.mutable.Specification

class MilestoneSpec extends Specification {

  "Milestone.apply" >> {
    val text = "id=10,rapidViewId=2,state=FUTURE,name=goalがないスプリント,goal=,startDate=<null>,endDate=2019-04-13T15:50:00.000Z,completeDate=<null>,sequence=10"
    val actual = Milestone(text)

    actual.id must equalTo(10)
    actual.name must equalTo("goalがないスプリント")
    actual.goal must beNone
    actual.startDate must beNone
    actual.endDate must beSome(DateUtil.yyyymmddParse("2019-04-13T15:50:00.000Z"))
  }

}
