package com.nulabinc.jira.client.apis.impl

import com.nulabinc.jira.client.HttpClient
import com.nulabinc.jira.client.apis.ProjectAPI
import com.nulabinc.jira.client.domain.Project
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class ProjectAPISpec extends Specification with Mockito {

  private val httpClient: HttpClient = smartMock[HttpClient]

  "should get a Project" >> {
    val response =
      """
        |{
        |    "expand": "description,lead,issueTypes,url,projectKeys",
        |    "self": "http://www.example.com/jira/rest/api/2/project/EX",
        |    "id": "10000",
        |    "key": "EX",
        |    "description": "This project was created as an example for REST.",
        |    "lead": {
        |        "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |        "key": "fred",
        |        "accountId": "99:27935d01-92a7-4687-8272-a9b8d3b2ae2e",
        |        "name": "fred",
        |        "avatarUrls": {
        |            "48x48": "http://www.example.com/jira/secure/useravatar?size=large&ownerId=fred",
        |            "24x24": "http://www.example.com/jira/secure/useravatar?size=small&ownerId=fred",
        |            "16x16": "http://www.example.com/jira/secure/useravatar?size=xsmall&ownerId=fred",
        |            "32x32": "http://www.example.com/jira/secure/useravatar?size=medium&ownerId=fred"
        |        },
        |        "displayName": "Fred F. User",
        |        "active": false
        |    },
        |    "components": [
        |        {
        |            "self": "http://www.example.com/jira/rest/api/2/component/10000",
        |            "id": "10000",
        |            "name": "Component 1",
        |            "description": "This is a JIRA component",
        |            "lead": {
        |                "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                "key": "fred",
        |                "accountId": "99:27935d01-92a7-4687-8272-a9b8d3b2ae2e",
        |                "name": "fred",
        |                "avatarUrls": {
        |                    "48x48": "http://www.example.com/jira/secure/useravatar?size=large&ownerId=fred",
        |                    "24x24": "http://www.example.com/jira/secure/useravatar?size=small&ownerId=fred",
        |                    "16x16": "http://www.example.com/jira/secure/useravatar?size=xsmall&ownerId=fred",
        |                    "32x32": "http://www.example.com/jira/secure/useravatar?size=medium&ownerId=fred"
        |                },
        |                "displayName": "Fred F. User",
        |                "active": false
        |            },
        |            "assigneeType": "PROJECT_LEAD",
        |            "assignee": {
        |                "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                "key": "fred",
        |                "accountId": "99:27935d01-92a7-4687-8272-a9b8d3b2ae2e",
        |                "name": "fred",
        |                "avatarUrls": {
        |                    "48x48": "http://www.example.com/jira/secure/useravatar?size=large&ownerId=fred",
        |                    "24x24": "http://www.example.com/jira/secure/useravatar?size=small&ownerId=fred",
        |                    "16x16": "http://www.example.com/jira/secure/useravatar?size=xsmall&ownerId=fred",
        |                    "32x32": "http://www.example.com/jira/secure/useravatar?size=medium&ownerId=fred"
        |                },
        |                "displayName": "Fred F. User",
        |                "active": false
        |            },
        |            "realAssigneeType": "PROJECT_LEAD",
        |            "realAssignee": {
        |                "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                "key": "fred",
        |                "accountId": "99:27935d01-92a7-4687-8272-a9b8d3b2ae2e",
        |                "name": "fred",
        |                "avatarUrls": {
        |                    "48x48": "http://www.example.com/jira/secure/useravatar?size=large&ownerId=fred",
        |                    "24x24": "http://www.example.com/jira/secure/useravatar?size=small&ownerId=fred",
        |                    "16x16": "http://www.example.com/jira/secure/useravatar?size=xsmall&ownerId=fred",
        |                    "32x32": "http://www.example.com/jira/secure/useravatar?size=medium&ownerId=fred"
        |                },
        |                "displayName": "Fred F. User",
        |                "active": false
        |            },
        |            "isAssigneeTypeValid": false,
        |            "project": "HSP",
        |            "projectId": 10000
        |        }
        |    ],
        |    "issueTypes": [
        |        {
        |            "self": "http://localhost:8090/jira/rest/api/2.0/issueType/3",
        |            "id": "3",
        |            "description": "A task that needs to be done.",
        |            "iconUrl": "http://localhost:8090/jira/images/icons/issuetypes/task.png",
        |            "name": "Task",
        |            "subtask": false,
        |            "avatarId": 1
        |        },
        |        {
        |            "self": "http://localhost:8090/jira/rest/api/2.0/issueType/1",
        |            "id": "1",
        |            "description": "A problem with the software.",
        |            "iconUrl": "http://localhost:8090/jira/images/icons/issuetypes/bug.png",
        |            "name": "Bug",
        |            "subtask": false,
        |            "avatarId": 10002
        |        }
        |    ],
        |    "url": "http://www.example.com/jira/browse/EX",
        |    "email": "from-jira@example.com",
        |    "assigneeType": "PROJECT_LEAD",
        |    "versions": [],
        |    "name": "Example",
        |    "roles": {
        |        "Developers": "http://www.example.com/jira/rest/api/2/project/EX/role/10000"
        |    },
        |    "avatarUrls": {
        |        "48x48": "http://www.example.com/jira/secure/projectavatar?size=large&pid=10000",
        |        "24x24": "http://www.example.com/jira/secure/projectavatar?size=small&pid=10000",
        |        "16x16": "http://www.example.com/jira/secure/projectavatar?size=xsmall&pid=10000",
        |        "32x32": "http://www.example.com/jira/secure/projectavatar?size=medium&pid=10000"
        |    },
        |    "projectCategory": {
        |        "self": "http://www.example.com/jira/rest/api/2/projectCategory/10000",
        |        "id": "10000",
        |        "name": "FIRST",
        |        "description": "First Project Category"
        |    }
        |}
      """.stripMargin

    httpClient.get(any[String]) returns Right(response)

    val client = new ProjectAPI(httpClient)
    val expect = Project(
      10000,
      "EX",
      "Example",
      "This project was created as an example for REST."
    )
    val actual = client.project(10000).right.get
    actual must equalTo(expect)
  }
}
