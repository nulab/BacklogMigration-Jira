package integration.matchers

import java.util.Date

import com.nulabinc.backlog.j2b.jira.utils.DatetimeToDateFormatter
import integration.helper.{DateFormatter, TestHelper}
import org.scalatest.{Assertion, Matchers}

trait DateMatcher
    extends TestHelper
    with Matchers
    with DateFormatter
    with DatetimeToDateFormatter {

  def assertDate(jiraDateString: String, backlogDate: Date): Assertion = {
    val jiraDate = dateTimeStringToDateString(jiraDateString)
    jiraDate should equal(dateToDateString(backlogDate))
  }

  def assertDateTime(jiraDateRimeString: String, backlogDate: Date): Assertion =
    jiraDateRimeString should equal(dateToDateString(backlogDate))

}
