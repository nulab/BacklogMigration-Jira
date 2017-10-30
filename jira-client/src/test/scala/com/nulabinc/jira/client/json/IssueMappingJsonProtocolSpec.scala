package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.domain.issue.Issue
import org.specs2.mutable.Specification
import spray.json._

class IssueMappingJsonProtocolSpec extends Specification {

  import com.nulabinc.jira.client.json.IssueMappingJsonProtocol._

  "IssueMappingJsonProtocol should return an Issue" >> {
    val actual = JsonParser(string).convertTo[Issue]

    actual.id must beEqualTo(10010)
    actual.key must beEqualTo("TEST-1")
    actual.description must beSome("Test issue #1")
    actual.assignee.get.name must beEqualTo("tanaka")
    actual.issueFields.length must beEqualTo(5)
    actual.timeTrack.get.originalEstimateSeconds must beSome(32400)
    actual.timeTrack.get.timeSpentSeconds must beSome(3720)
    actual.components.length must beEqualTo(1)
  }

  def string =
    """
      |{
      |  "expand": "renderedFields,names,schema,operations,editmeta,changelog,versionedRepresentations",
      |  "id": "10010",
      |  "self": "https://test-site.atlassian.net/rest/api/2/issue/10010",
      |  "key": "TEST-1",
      |  "fields": {
      |      "issuetype": {
      |      "self": "https://test-site.atlassian.net/rest/api/2/issuetype/10002",
      |      "id": "10002",
      |      "description": "The sub-task of the issue",
      |      "name": "Sub-task",
      |      "subtask": true,
      |      "avatarId": 10316
      |    },
      |    "description": "Test issue #1",
      |    "assignee": {
      |      "self": "https://test-site.atlassian.net/rest/api/2/user?username=tanaka",
      |      "name": "tanaka",
      |      "key": "tanaka",
      |      "accountId": "557058:236c164c-9007-4cec-b067-08b82f7c437a",
      |      "emailAddress": "tanaka@test-inc.com",
      |      "displayName": "tanaka",
      |      "active": true,
      |      "timeZone": "Asia/Tokyo"
      |    },
      |    "customfield_10060": null,
      |    "customfield_10050": "2017-10-25T18:10:00.000+0900",
      |    "customfield_10051": [
      |      "aaa",
      |      "bbb"
      |    ],
      |    "customfield_10054": [
      |      {
      |        "self": "https://test-site.atlassian.net/rest/api/2/customFieldOption/10010",
      |        "value": "選択肢1",
      |        "id": "10010"
      |      },
      |      {
      |        "self": "https://test-site.atlassian.net/rest/api/2/customFieldOption/10011",
      |        "value": "選択肢2",
      |        "id": "10011"
      |      }
      |    ],
      |    "customfield_10000": "{}",
      |    "customfield_10059": {
      |      "self": "https://test-site.atlassian.net/rest/api/2/user?username=nakamura",
      |      "name": "nakamura",
      |      "key": "nakamura",
      |      "accountId": "557058:3a0d29cb-3c91-4bd8-8970-48b7c089a12d",
      |      "emailAddress": "nakamura@test-inc",
      |      "displayName": "ikikko",
      |      "active": true,
      |      "timeZone": "Asia/Tokyo"
      |    },
      |    "timetracking": {
      |      "originalEstimate": "1d 1h",
      |      "remainingEstimate": "1h",
      |      "originalEstimateSeconds": 32400,
      |      "remainingEstimateSeconds": 3600,
      |      "timeSpentSeconds": 3720
      |    },
      |
      |    "components": [
      |      {
      |        "self": "https://test-site.atlassian.net/rest/api/2/component/10002",
      |        "id": "10002",
      |        "name": "設計"
      |      }
      |    ]
      |  }
      |}
    """.stripMargin
}
