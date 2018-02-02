package com.nulabinc.backlog.j2b.matchers

import com.nulabinc.backlog.j2b.helper.TestHelper
import com.nulabinc.backlog4j.{User => BacklogUser}
import com.nulabinc.jira.client.domain.{User => JiraUser}
import org.scalatest.{Assertion, Matchers}

trait UserMatcher extends TestHelper with Matchers {

  def assertUser(jiraUser: JiraUser, backlogUser: BacklogUser): Assertion =
    convertUser(jiraUser.identifyKey) should equal(backlogUser.getUserId)

  def assertUser(jiraUserKey: String, backlogUserId: String): Assertion =
    convertUser(jiraUserKey) should equal(backlogUserId)
}
