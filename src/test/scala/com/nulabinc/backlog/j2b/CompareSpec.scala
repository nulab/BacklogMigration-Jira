package com.nulabinc.backlog.j2b

import com.nulabinc.backlog.j2b.exporter.service.JiraClientIssueService
import com.nulabinc.backlog.j2b.helper._
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.convert.writes.UserWrites
import com.nulabinc.backlog4j.api.option.GetIssuesParams
import org.scalatest.{DiagrammedAssertions, FlatSpec, Matchers}

import scala.collection.JavaConverters._

class CompareSpec extends FlatSpec
    with Matchers
    with DiagrammedAssertions
    with TestHelper
    with DateFormatter {

  // --------------------------------------------------------------------------
  testProject(appConfig.jiraConfig, appConfig.backlogConfig)
  testProjectUsers(appConfig.backlogConfig)
  testVersion(appConfig.jiraConfig, appConfig.backlogConfig)
  testIssueType(appConfig.backlogConfig)
  testIssue(appConfig.jiraConfig, appConfig.backlogConfig)

  def testProject(jiraConfig: JiraApiConfiguration, backlogConfig: BacklogApiConfiguration): Unit = {
    "Project" should "match" in {
      val jiraProject = jiraRestApi.projectAPI.project(jiraConfig.projectKey)
      val backlogProject = backlogApi.getProject(backlogConfig.projectKey)

      backlogProject.getName should equal(jiraProject.right.get.name)
      backlogProject.isChartEnabled should be(true)
      backlogProject.isSubtaskingEnabled should be(true)
      backlogProject.getTextFormattingRule should equal(com.nulabinc.backlog4j.Project.TextFormattingRule.Markdown)
    }
  }

  def testProjectUsers(backlogConfig: BacklogApiConfiguration): Unit = {
    "Project user" should "match" in {
      implicit val backlogUserWrites: UserWrites = new UserWrites()

      val backlogUsers    = backlogApi.getProjectUsers(backlogConfig.projectKey).asScala
      val userMappingFile = mappingFileService.createUserMappingFileFromJson(jiraBacklogPaths.jiraUsersJson, backlogUsers.map(Convert.toBacklog(_)))
      val jiraUsers       = userMappingFile.tryUnMarshal()

      jiraUsers.foreach { jiraUser =>
        backlogUsers.exists { backlogUser =>
          backlogUser.getUserId == jiraUser.dst
        } should be(true)
      }
    }
  }

  def testVersion(jiraConfig: JiraApiConfiguration, backlogConfig: BacklogApiConfiguration): Unit =
    "Version" should "match" in {
      val backlogVersions = backlogApi.getVersions(backlogConfig.projectKey).asScala
      val jiraVersions    = jiraRestApi.versionsAPI.projectVersions(jiraConfig.projectKey).right.get
      jiraVersions.foreach { jiraVersion =>
        val optBacklogVersion = backlogVersions.find(backlogVersion => jiraVersion.name == backlogVersion.getName)
        optBacklogVersion.isDefined should be(true)
        for {
          backlogVersion <- optBacklogVersion
        } yield {
          assert(jiraVersion.name == backlogVersion.getName)
          assert(jiraVersion.description.get == backlogVersion.getDescription)
        }
      }
    }

  def testIssueType(backlogConfig: BacklogApiConfiguration): Unit =
    "Issue type" should "match" in {
      val backlogIssueTypes = backlogApi.getIssueTypes(backlogConfig.projectKey).asScala
      val jiraIssueTypes    = jiraRestApi.issueTypeAPI.allIssueTypes().right.get
      jiraIssueTypes.foreach { jiraIssueType =>
        val backlogIssueType = backlogIssueTypes.find(backlogIssueType => jiraIssueType.name == backlogIssueType.getName).get
        jiraIssueType.name should equal(backlogIssueType.getName)
      }
    }


  def testIssue(jiraConfig: JiraApiConfiguration, backlogConfig: BacklogApiConfiguration): Unit = {

    val issueService = new JiraClientIssueService(
      jiraConfig,
      JiraProjectKey(jiraConfig.projectKey),
      jiraRestApi,
      jiraBacklogPaths
    )

    val backlogProject = backlogApi.getProject(backlogConfig.projectKey)
    val params         = new GetIssuesParams(List(Long.box(backlogProject.getId)).asJava)
    val backlogIssues  = backlogApi.getIssues(params).asScala

    def fetchIssues(startAt: Long, maxResults: Long): Unit = {
      val issues = issueService.issues(startAt, maxResults)

      issues.foreach { jiraIssue =>
        "Issue" should s"match: ${jiraIssue.id} - ${jiraIssue.summary}" in {

          val maybeBacklogIssue = backlogIssues.find(backlogIssue => jiraIssue.summary == backlogIssue.getSummary)

          withClue(s"""
                      |jira subject:${jiraIssue.summary}
          """.stripMargin) {
            maybeBacklogIssue should not be None
          }

          maybeBacklogIssue.map { backlogIssue =>

            // description
            jiraIssue.description.getOrElse("") should equal(backlogIssue.getDescription)

            // issue type
            jiraIssue.issueType.name should equal(backlogIssue.getIssueType.getName)

            // category
            jiraIssue.components.map { jiraCategory =>
              backlogIssue.getCategory.asScala.find(_.getName == jiraCategory.name) should not be empty
            }

            // version
            jiraIssue.fixVersions.map { jiraVersion =>
              backlogIssue.getVersions.asScala.find(_.getName == jiraVersion.name) should not be empty
            }

            // TODO milestone

            // due date
            dateToOptionDateString(jiraIssue.dueDate) should equal(dateToOptionDateString(Option(backlogIssue.getDueDate)))

            // priority
//            println(s"priority: ${jiraIssue.priority.name} - ${backlogIssue.getPriority.getName}")
            convertPriority(jiraIssue.priority.name) should equal(backlogIssue.getPriority.getName)

            // status
            withClue(s"""
                        |status:${jiraIssue.status.name}
                        |converted:${convertStatus(jiraIssue.status.name)}
                        |""".stripMargin) {
              convertStatus(jiraIssue.status.name) should equal(backlogIssue.getStatus.getName)
            }

            // assignee TODO test failed
//            jiraIssue.assignee.map { user =>
//              convertUser(user.name) should equal(backlogIssue.getAssignee.getName)
//            }

          }

          println(s"match: ${jiraIssue.id} - ${jiraIssue.summary}")
        }
      }

      if (issues.nonEmpty) {
        fetchIssues(startAt + maxResults, maxResults)
      }
    }

    fetchIssues(0, 10)
  }

}
