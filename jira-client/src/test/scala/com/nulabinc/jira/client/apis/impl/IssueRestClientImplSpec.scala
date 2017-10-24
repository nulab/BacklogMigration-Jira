package com.nulabinc.jira.client.apis.impl

import com.nulabinc.jira.client.HttpClient
import com.nulabinc.jira.client.domain.{Issue, IssueField, User}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class IssueRestClientImplSpec extends Specification with Mockito {

  "should get an Issue" >> {

    val response =
      """
        |{
        |    "expand": "renderedFields,names,schema,operations,editmeta,changelog,versionedRepresentations",
        |    "id": "10002",
        |    "self": "http://www.example.com/jira/rest/api/2/issue/10002",
        |    "key": "EX-1",
        |    "names": {
        |        "watcher": "watcher",
        |        "attachment": "attachment",
        |        "sub-tasks": "sub-tasks",
        |        "description": "description",
        |        "project": "project",
        |        "comment": "comment",
        |        "issuelinks": "issuelinks",
        |        "worklog": "worklog",
        |        "updated": "updated",
        |        "timetracking": "timetracking"
        |    },
        |    "schema": {},
        |    "fields": {
        |        "watcher": {
        |            "self": "http://www.example.com/jira/rest/api/2/issue/EX-1/watchers",
        |            "isWatching": false,
        |            "watchCount": 1,
        |            "watchers": [
        |                {
        |                    "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                    "name": "fred",
        |                    "displayName": "Fred F. User",
        |                    "active": false
        |                }
        |            ]
        |        },
        |        "attachment": [
        |            {
        |                "id": 10001,
        |                "self": "http://www.example.com/jira/rest/api/2.0/attachments/10000",
        |                "filename": "picture.jpg",
        |                "author": {
        |                    "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                    "key": "fred",
        |                    "accountId": "99:27935d01-92a7-4687-8272-a9b8d3b2ae2e",
        |                    "name": "fred",
        |                    "avatarUrls": {
        |                        "48x48": "http://www.example.com/jira/secure/useravatar?size=large&ownerId=fred",
        |                        "24x24": "http://www.example.com/jira/secure/useravatar?size=small&ownerId=fred",
        |                        "16x16": "http://www.example.com/jira/secure/useravatar?size=xsmall&ownerId=fred",
        |                        "32x32": "http://www.example.com/jira/secure/useravatar?size=medium&ownerId=fred"
        |                    },
        |                    "displayName": "Fred F. User",
        |                    "active": false
        |                },
        |                "created": "2017-10-16T05:30:15.868+0000",
        |                "size": 23123,
        |                "mimeType": "image/jpeg",
        |                "content": "http://www.example.com/jira/attachments/10000",
        |                "thumbnail": "http://www.example.com/jira/secure/thumbnail/10000"
        |            }
        |        ],
        |        "sub-tasks": [
        |            {
        |                "id": "10000",
        |                "type": {
        |                    "id": "10000",
        |                    "name": "",
        |                    "inward": "Parent",
        |                    "outward": "Sub-task"
        |                },
        |                "outwardIssue": {
        |                    "id": "10003",
        |                    "key": "EX-2",
        |                    "self": "http://www.example.com/jira/rest/api/2/issue/EX-2",
        |                    "fields": {
        |                        "status": {
        |                            "iconUrl": "http://www.example.com/jira//images/icons/statuses/open.png",
        |                            "name": "Open"
        |                        }
        |                    }
        |                }
        |            }
        |        ],
        |        "description": "example bug report",
        |        "project": {
        |            "self": "http://www.example.com/jira/rest/api/2/project/EX",
        |            "id": "10000",
        |            "key": "EX",
        |            "name": "Example",
        |            "avatarUrls": {
        |                "48x48": "http://www.example.com/jira/secure/projectavatar?size=large&pid=10000",
        |                "24x24": "http://www.example.com/jira/secure/projectavatar?size=small&pid=10000",
        |                "16x16": "http://www.example.com/jira/secure/projectavatar?size=xsmall&pid=10000",
        |                "32x32": "http://www.example.com/jira/secure/projectavatar?size=medium&pid=10000"
        |            },
        |            "projectCategory": {
        |                "self": "http://www.example.com/jira/rest/api/2/projectCategory/10000",
        |                "id": "10000",
        |                "name": "FIRST",
        |                "description": "First Project Category"
        |            }
        |        },
        |        "comment": [
        |            {
        |                "self": "http://www.example.com/jira/rest/api/2/issue/10010/comment/10000",
        |                "id": "10000",
        |                "author": {
        |                    "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                    "name": "fred",
        |                    "displayName": "Fred F. User",
        |                    "active": false
        |                },
        |                "body": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque eget venenatis elit. Duis eu justo eget augue iaculis fermentum. Sed semper quam laoreet nisi egestas at posuere augue semper.",
        |                "updateAuthor": {
        |                    "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                    "name": "fred",
        |                    "displayName": "Fred F. User",
        |                    "active": false
        |                },
        |                "created": "2017-10-16T05:30:15.120+0000",
        |                "updated": "2017-10-16T05:30:15.120+0000",
        |                "visibility": {
        |                    "type": "role",
        |                    "value": "Administrators"
        |                }
        |            }
        |        ],
        |        "issuelinks": [
        |            {
        |                "id": "10001",
        |                "type": {
        |                    "id": "10000",
        |                    "name": "Dependent",
        |                    "inward": "depends on",
        |                    "outward": "is depended by"
        |                },
        |                "outwardIssue": {
        |                    "id": "10004L",
        |                    "key": "PRJ-2",
        |                    "self": "http://www.example.com/jira/rest/api/2/issue/PRJ-2",
        |                    "fields": {
        |                        "status": {
        |                            "iconUrl": "http://www.example.com/jira//images/icons/statuses/open.png",
        |                            "name": "Open"
        |                        }
        |                    }
        |                }
        |            },
        |            {
        |                "id": "10002",
        |                "type": {
        |                    "id": "10000",
        |                    "name": "Dependent",
        |                    "inward": "depends on",
        |                    "outward": "is depended by"
        |                },
        |                "inwardIssue": {
        |                    "id": "10004",
        |                    "key": "PRJ-3",
        |                    "self": "http://www.example.com/jira/rest/api/2/issue/PRJ-3",
        |                    "fields": {
        |                        "status": {
        |                            "iconUrl": "http://www.example.com/jira//images/icons/statuses/open.png",
        |                            "name": "Open"
        |                        }
        |                    }
        |                }
        |            }
        |        ],
        |        "worklog": [
        |            {
        |                "self": "http://www.example.com/jira/rest/api/2/issue/10010/worklog/10000",
        |                "author": {
        |                    "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                    "name": "fred",
        |                    "displayName": "Fred F. User",
        |                    "active": false
        |                },
        |                "updateAuthor": {
        |                    "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                    "name": "fred",
        |                    "displayName": "Fred F. User",
        |                    "active": false
        |                },
        |                "comment": "I did some work here.",
        |                "updated": "2017-10-16T05:30:15.890+0000",
        |                "visibility": {
        |                    "type": "group",
        |                    "value": "jira-developers"
        |                },
        |                "started": "2017-10-16T05:30:15.890+0000",
        |                "timeSpent": "3h 20m",
        |                "timeSpentSeconds": 12000,
        |                "id": "100028",
        |                "issueId": "10002"
        |            }
        |        ],
        |        "updated": 1,
        |        "timetracking": {
        |            "originalEstimate": "10m",
        |            "remainingEstimate": "3m",
        |            "timeSpent": "6m",
        |            "originalEstimateSeconds": 600,
        |            "remainingEstimateSeconds": 200,
        |            "timeSpentSeconds": 400
        |        },
        |        "assignee": {
        |           "name": "User1",
        |           "key": "User1",
        |           "accountId": "557058:236c164c-9007-4cec-b067-08b82f7c437a",
        |           "emailAddress": "test@nulab.com",
        |           "displayName": "User1 name",
        |           "active": true,
        |           "timeZone": "Asia/Tokyo"
        |       }
        |    }
        |}
      """.stripMargin

    val httpClient: HttpClient = smartMock[HttpClient]
    httpClient.get(any[String]) returns Right(response)

    val client = new IssueRestClientImpl(httpClient)
    val expect = Issue(
      id = 10002,
      key = "EX-1",
      description = Some("example bug report"),
      assignee = Some(User(
        name = "User1",
        displayName = "User1 name"
      )),
      issueFields = Seq.empty[IssueField]
    )
    val actual = client.issue(10002).right.get

    actual must equalTo(expect)
  }

  "should get issues belonging to the project" >> {

    val response =
      """
        |{
        |    "expand": "schema,names",
        |    "startAt": 0,
        |    "maxResults": 2,
        |    "total": 7,
        |    "issues": [
        |        {
        |            "expand": "renderedFields,names,schema,operations,editmeta,changelog,versionedRepresentations",
        |            "id": "10002",
        |            "self": "http://www.example.com/jira/rest/api/2/issue/10002",
        |            "key": "EX-1",
        |            "names": {
        |                "watcher": "watcher",
        |                "attachment": "attachment",
        |                "sub-tasks": "sub-tasks",
        |                "description": "description",
        |                "project": "project",
        |                "comment": "comment",
        |                "issuelinks": "issuelinks",
        |                "worklog": "worklog",
        |                "updated": "updated",
        |                "timetracking": "timetracking"
        |            },
        |            "schema": {},
        |            "fields": {
        |                "watcher": {
        |                    "self": "http://www.example.com/jira/rest/api/2/issue/EX-1/watchers",
        |                    "isWatching": false,
        |                    "watchCount": 1,
        |                    "watchers": [
        |                        {
        |                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                            "name": "fred",
        |                            "displayName": "Fred F. User",
        |                            "active": false
        |                        }
        |                    ]
        |                },
        |                "attachment": [
        |                    {
        |                        "id": 10001,
        |                        "self": "http://www.example.com/jira/rest/api/2.0/attachments/10000",
        |                        "filename": "picture.jpg",
        |                        "author": {
        |                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                            "key": "fred",
        |                            "accountId": "99:27935d01-92a7-4687-8272-a9b8d3b2ae2e",
        |                            "name": "fred",
        |                            "avatarUrls": {
        |                                "48x48": "http://www.example.com/jira/secure/useravatar?size=large&ownerId=fred",
        |                                "24x24": "http://www.example.com/jira/secure/useravatar?size=small&ownerId=fred",
        |                                "16x16": "http://www.example.com/jira/secure/useravatar?size=xsmall&ownerId=fred",
        |                                "32x32": "http://www.example.com/jira/secure/useravatar?size=medium&ownerId=fred"
        |                            },
        |                            "displayName": "Fred F. User",
        |                            "active": false
        |                        },
        |                        "created": "2017-10-16T05:30:15.868+0000",
        |                        "size": 23123,
        |                        "mimeType": "image/jpeg",
        |                        "content": "http://www.example.com/jira/attachments/10000",
        |                        "thumbnail": "http://www.example.com/jira/secure/thumbnail/10000"
        |                    }
        |                ],
        |                "sub-tasks": [
        |                    {
        |                        "id": "10000",
        |                        "type": {
        |                            "id": "10000",
        |                            "name": "",
        |                            "inward": "Parent",
        |                            "outward": "Sub-task"
        |                        },
        |                        "outwardIssue": {
        |                            "id": "10003",
        |                            "key": "EX-2",
        |                            "self": "http://www.example.com/jira/rest/api/2/issue/EX-2",
        |                            "fields": {
        |                                "status": {
        |                                    "iconUrl": "http://www.example.com/jira//images/icons/statuses/open.png",
        |                                    "name": "Open"
        |                                }
        |                            }
        |                        }
        |                    }
        |                ],
        |                "description": "example bug report",
        |                "project": {
        |                    "self": "http://www.example.com/jira/rest/api/2/project/EX",
        |                    "id": "10000",
        |                    "key": "EX",
        |                    "name": "Example",
        |                    "avatarUrls": {
        |                        "48x48": "http://www.example.com/jira/secure/projectavatar?size=large&pid=10000",
        |                        "24x24": "http://www.example.com/jira/secure/projectavatar?size=small&pid=10000",
        |                        "16x16": "http://www.example.com/jira/secure/projectavatar?size=xsmall&pid=10000",
        |                        "32x32": "http://www.example.com/jira/secure/projectavatar?size=medium&pid=10000"
        |                    },
        |                    "projectCategory": {
        |                        "self": "http://www.example.com/jira/rest/api/2/projectCategory/10000",
        |                        "id": "10000",
        |                        "name": "FIRST",
        |                        "description": "First Project Category"
        |                    }
        |                },
        |                "comment": [
        |                    {
        |                        "self": "http://www.example.com/jira/rest/api/2/issue/10010/comment/10000",
        |                        "id": "10000",
        |                        "author": {
        |                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                            "name": "fred",
        |                            "displayName": "Fred F. User",
        |                            "active": false
        |                        },
        |                        "body": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque eget venenatis elit. Duis eu justo eget augue iaculis fermentum. Sed semper quam laoreet nisi egestas at posuere augue semper.",
        |                        "updateAuthor": {
        |                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                            "name": "fred",
        |                            "displayName": "Fred F. User",
        |                            "active": false
        |                        },
        |                        "created": "2017-10-16T05:30:15.120+0000",
        |                        "updated": "2017-10-16T05:30:15.120+0000",
        |                        "visibility": {
        |                            "type": "role",
        |                            "value": "Administrators"
        |                        }
        |                    }
        |                ],
        |                "issuelinks": [
        |                    {
        |                        "id": "10001",
        |                        "type": {
        |                            "id": "10000",
        |                            "name": "Dependent",
        |                            "inward": "depends on",
        |                            "outward": "is depended by"
        |                        },
        |                        "outwardIssue": {
        |                            "id": "10004L",
        |                            "key": "PRJ-2",
        |                            "self": "http://www.example.com/jira/rest/api/2/issue/PRJ-2",
        |                            "fields": {
        |                                "status": {
        |                                    "iconUrl": "http://www.example.com/jira//images/icons/statuses/open.png",
        |                                    "name": "Open"
        |                                }
        |                            }
        |                        }
        |                    },
        |                    {
        |                        "id": "10002",
        |                        "type": {
        |                            "id": "10000",
        |                            "name": "Dependent",
        |                            "inward": "depends on",
        |                            "outward": "is depended by"
        |                        },
        |                        "inwardIssue": {
        |                            "id": "10004",
        |                            "key": "PRJ-3",
        |                            "self": "http://www.example.com/jira/rest/api/2/issue/PRJ-3",
        |                            "fields": {
        |                                "status": {
        |                                    "iconUrl": "http://www.example.com/jira//images/icons/statuses/open.png",
        |                                    "name": "Open"
        |                                }
        |                            }
        |                        }
        |                    }
        |                ],
        |                "worklog": [
        |                    {
        |                        "self": "http://www.example.com/jira/rest/api/2/issue/10010/worklog/10000",
        |                        "author": {
        |                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                            "name": "fred",
        |                            "displayName": "Fred F. User",
        |                            "active": false
        |                        },
        |                        "updateAuthor": {
        |                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                            "name": "fred",
        |                            "displayName": "Fred F. User",
        |                            "active": false
        |                        },
        |                        "comment": "I did some work here.",
        |                        "updated": "2017-10-16T05:30:15.890+0000",
        |                        "visibility": {
        |                            "type": "group",
        |                            "value": "jira-developers"
        |                        },
        |                        "started": "2017-10-16T05:30:15.890+0000",
        |                        "timeSpent": "3h 20m",
        |                        "timeSpentSeconds": 12000,
        |                        "id": "100028",
        |                        "issueId": "10002"
        |                    }
        |                ],
        |                "updated": 1,
        |                "timetracking": {
        |                    "originalEstimate": "10m",
        |                    "remainingEstimate": "3m",
        |                    "timeSpent": "6m",
        |                    "originalEstimateSeconds": 600,
        |                    "remainingEstimateSeconds": 200,
        |                    "timeSpentSeconds": 400
        |                }
        |            }
        |        },
        |                {
        |            "expand": "renderedFields,names,schema,operations,editmeta,changelog,versionedRepresentations",
        |            "id": "10003",
        |            "self": "http://www.example.com/jira/rest/api/2/issue/10002",
        |            "key": "EX-2",
        |            "names": {
        |                "watcher": "watcher",
        |                "attachment": "attachment",
        |                "sub-tasks": "sub-tasks",
        |                "description": "description",
        |                "project": "project",
        |                "comment": "comment",
        |                "issuelinks": "issuelinks",
        |                "worklog": "worklog",
        |                "updated": "updated",
        |                "timetracking": "timetracking"
        |            },
        |            "schema": {},
        |            "fields": {
        |                "watcher": {
        |                    "self": "http://www.example.com/jira/rest/api/2/issue/EX-1/watchers",
        |                    "isWatching": false,
        |                    "watchCount": 1,
        |                    "watchers": [
        |                        {
        |                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                            "name": "fred",
        |                            "displayName": "Fred F. User",
        |                            "active": false
        |                        }
        |                    ]
        |                },
        |                "attachment": [
        |                    {
        |                        "id": 10001,
        |                        "self": "http://www.example.com/jira/rest/api/2.0/attachments/10000",
        |                        "filename": "picture.jpg",
        |                        "author": {
        |                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                            "key": "fred",
        |                            "accountId": "99:27935d01-92a7-4687-8272-a9b8d3b2ae2e",
        |                            "name": "fred",
        |                            "avatarUrls": {
        |                                "48x48": "http://www.example.com/jira/secure/useravatar?size=large&ownerId=fred",
        |                                "24x24": "http://www.example.com/jira/secure/useravatar?size=small&ownerId=fred",
        |                                "16x16": "http://www.example.com/jira/secure/useravatar?size=xsmall&ownerId=fred",
        |                                "32x32": "http://www.example.com/jira/secure/useravatar?size=medium&ownerId=fred"
        |                            },
        |                            "displayName": "Fred F. User",
        |                            "active": false
        |                        },
        |                        "created": "2017-10-16T05:30:15.868+0000",
        |                        "size": 23123,
        |                        "mimeType": "image/jpeg",
        |                        "content": "http://www.example.com/jira/attachments/10000",
        |                        "thumbnail": "http://www.example.com/jira/secure/thumbnail/10000"
        |                    }
        |                ],
        |                "sub-tasks": [
        |                    {
        |                        "id": "10000",
        |                        "type": {
        |                            "id": "10000",
        |                            "name": "",
        |                            "inward": "Parent",
        |                            "outward": "Sub-task"
        |                        },
        |                        "outwardIssue": {
        |                            "id": "10003",
        |                            "key": "EX-2",
        |                            "self": "http://www.example.com/jira/rest/api/2/issue/EX-2",
        |                            "fields": {
        |                                "status": {
        |                                    "iconUrl": "http://www.example.com/jira//images/icons/statuses/open.png",
        |                                    "name": "Open"
        |                                }
        |                            }
        |                        }
        |                    }
        |                ],
        |                "description": "example bug report 2",
        |                "project": {
        |                    "self": "http://www.example.com/jira/rest/api/2/project/EX",
        |                    "id": "10000",
        |                    "key": "EX",
        |                    "name": "Example",
        |                    "avatarUrls": {
        |                        "48x48": "http://www.example.com/jira/secure/projectavatar?size=large&pid=10000",
        |                        "24x24": "http://www.example.com/jira/secure/projectavatar?size=small&pid=10000",
        |                        "16x16": "http://www.example.com/jira/secure/projectavatar?size=xsmall&pid=10000",
        |                        "32x32": "http://www.example.com/jira/secure/projectavatar?size=medium&pid=10000"
        |                    },
        |                    "projectCategory": {
        |                        "self": "http://www.example.com/jira/rest/api/2/projectCategory/10000",
        |                        "id": "10000",
        |                        "name": "FIRST",
        |                        "description": "First Project Category"
        |                    }
        |                },
        |                "comment": [
        |                    {
        |                        "self": "http://www.example.com/jira/rest/api/2/issue/10010/comment/10000",
        |                        "id": "10000",
        |                        "author": {
        |                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                            "name": "fred",
        |                            "displayName": "Fred F. User",
        |                            "active": false
        |                        },
        |                        "body": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque eget venenatis elit. Duis eu justo eget augue iaculis fermentum. Sed semper quam laoreet nisi egestas at posuere augue semper.",
        |                        "updateAuthor": {
        |                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                            "name": "fred",
        |                            "displayName": "Fred F. User",
        |                            "active": false
        |                        },
        |                        "created": "2017-10-16T05:30:15.120+0000",
        |                        "updated": "2017-10-16T05:30:15.120+0000",
        |                        "visibility": {
        |                            "type": "role",
        |                            "value": "Administrators"
        |                        }
        |                    }
        |                ],
        |                "issuelinks": [
        |                    {
        |                        "id": "10001",
        |                        "type": {
        |                            "id": "10000",
        |                            "name": "Dependent",
        |                            "inward": "depends on",
        |                            "outward": "is depended by"
        |                        },
        |                        "outwardIssue": {
        |                            "id": "10004L",
        |                            "key": "PRJ-2",
        |                            "self": "http://www.example.com/jira/rest/api/2/issue/PRJ-2",
        |                            "fields": {
        |                                "status": {
        |                                    "iconUrl": "http://www.example.com/jira//images/icons/statuses/open.png",
        |                                    "name": "Open"
        |                                }
        |                            }
        |                        }
        |                    },
        |                    {
        |                        "id": "10002",
        |                        "type": {
        |                            "id": "10000",
        |                            "name": "Dependent",
        |                            "inward": "depends on",
        |                            "outward": "is depended by"
        |                        },
        |                        "inwardIssue": {
        |                            "id": "10004",
        |                            "key": "PRJ-3",
        |                            "self": "http://www.example.com/jira/rest/api/2/issue/PRJ-3",
        |                            "fields": {
        |                                "status": {
        |                                    "iconUrl": "http://www.example.com/jira//images/icons/statuses/open.png",
        |                                    "name": "Open"
        |                                }
        |                            }
        |                        }
        |                    }
        |                ],
        |                "worklog": [
        |                    {
        |                        "self": "http://www.example.com/jira/rest/api/2/issue/10010/worklog/10000",
        |                        "author": {
        |                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                            "name": "fred",
        |                            "displayName": "Fred F. User",
        |                            "active": false
        |                        },
        |                        "updateAuthor": {
        |                            "self": "http://www.example.com/jira/rest/api/2/user?username=fred",
        |                            "name": "fred",
        |                            "displayName": "Fred F. User",
        |                            "active": false
        |                        },
        |                        "comment": "I did some work here.",
        |                        "updated": "2017-10-16T05:30:15.890+0000",
        |                        "visibility": {
        |                            "type": "group",
        |                            "value": "jira-developers"
        |                        },
        |                        "started": "2017-10-16T05:30:15.890+0000",
        |                        "timeSpent": "3h 20m",
        |                        "timeSpentSeconds": 12000,
        |                        "id": "100028",
        |                        "issueId": "10002"
        |                    }
        |                ],
        |                "updated": 1,
        |                "timetracking": {
        |                    "originalEstimate": "10m",
        |                    "remainingEstimate": "3m",
        |                    "timeSpent": "6m",
        |                    "originalEstimateSeconds": 600,
        |                    "remainingEstimateSeconds": 200,
        |                    "timeSpentSeconds": 400
        |                },
        |                "assignee": {
        |                  "name": "User2",
        |                  "key": "User2",
        |                  "accountId": "557058:236c164c-9007-4cec-b067-08b82f7c437a",
        |                  "emailAddress": "test@nulab.com",
        |                  "displayName": "User2 name",
        |                  "active": true,
        |                  "timeZone": "Asia/Tokyo"
        |               }
        |            }
        |        }
        |    ]
        |}
      """.stripMargin

    val emptyResponse =
      """
        |{
        |    "startAt": 100,
        |    "maxResults": 100,
        |    "total": 2,
        |    "issues": []
        |}
      """.stripMargin

    val httpClient: HttpClient = smartMock[HttpClient]
    httpClient.get("/search?startAt=0&maxResults=100&jql=project%3DEX") returns Right(response)
    httpClient.get("/search?startAt=100&maxResults=100&jql=project%3DEX") returns Right(emptyResponse)

    val client = new IssueRestClientImpl(httpClient)
    val expect = Seq(
      Issue(
        id = 10002,
        key = "EX-1",
        description = Some("example bug report"),
        assignee = None,
        issueFields = Seq.empty[IssueField]
      ),
      Issue(
        id = 10003,
        key = "EX-2",
        description = Some("example bug report 2"),
        assignee = Some(User(
          name = "User2",
          displayName = "User2 name"
        )),
        issueFields = Seq.empty[IssueField]
      )
    )
    val actual = client.projectIssues("EX").right.get

    actual must equalTo(expect)
  }

}

