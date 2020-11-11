package integration

import java.io.File

import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.jira.domain.export._
import com.nulabinc.backlog.j2b.jira.domain.mapping.JiraUserMappingItem
import com.nulabinc.backlog.j2b.jira.domain.{FieldConverter, IssueFieldConverter}
import com.nulabinc.backlog.migration.common.conf.{
  BacklogApiConfiguration,
  BacklogConstantValue,
  MappingDirectory
}
import com.nulabinc.backlog.migration.common.convert.writes.UserWrites
import com.nulabinc.backlog.migration.common.services.UserMappingFileService
import com.nulabinc.backlog4j.api.option.{GetIssuesParams, QueryParams}
import com.nulabinc.backlog4j.internal.json.customFields._
import com.nulabinc.backlog4j.{CustomFieldSetting, IssueComment}
import com.nulabinc.jira.client.domain.changeLog.LinkChangeLogItemField
import integration.helper.{DateFormatter, TestHelper}
import integration.matchers.{DateMatcher, UserMatcher}
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import org.scalatest.{DiagrammedAssertions, FlatSpec, Matchers}

import scala.collection.JavaConverters._
import scala.collection.mutable

class CompareSpec
    extends FlatSpec
    with Matchers
    with DiagrammedAssertions
    with TestHelper
    with DateFormatter
    with UserMatcher
    with DateMatcher {

  import com.nulabinc.backlog.j2b.codec.JiraMappingDecoder._

  val jiraCustomFieldDefinitions: Seq[Field] =
    FieldConverter.toExportField(jiraRestApi.fieldAPI.all().right.get)
  val backlogCustomFieldDefinitions: mutable.Seq[CustomFieldSetting] =
    backlogApi.getCustomFields(appConfig.backlogConfig.projectKey).asScala

  // --------------------------------------------------------------------------
  testProject(appConfig.jiraConfig, appConfig.backlogConfig)
  testProjectUsers(appConfig.backlogConfig)
  testVersion(appConfig.jiraConfig, appConfig.backlogConfig)
  testIssueType(appConfig.backlogConfig)
  testCategory(appConfig.jiraConfig, appConfig.backlogConfig)
  testCustomFieldDefinitions(appConfig.backlogConfig)
  testIssue(appConfig.backlogConfig)

  def testProject(
      jiraConfig: JiraApiConfiguration,
      backlogConfig: BacklogApiConfiguration
  ): Unit = {
    "Project" should "match" in {
      val jiraProject    = jiraRestApi.projectAPI.project(jiraConfig.projectKey)
      val backlogProject = backlogApi.getProject(backlogConfig.projectKey)

      backlogProject.getName should equal(jiraProject.right.get.name)
      backlogProject.isChartEnabled should be(true)
      backlogProject.isSubtaskingEnabled should be(true)
      backlogProject.getTextFormattingRule should equal(
        com.nulabinc.backlog4j.Project.TextFormattingRule.Markdown
      )
    }
  }

  def testProjectUsers(backlogConfig: BacklogApiConfiguration): Unit = {
    "Project user" should "match" in {
      implicit val backlogUserWrites: UserWrites = new UserWrites()

      val backlogUsers =
        backlogApi.getProjectUsers(backlogConfig.projectKey).asScala
      val jiraUsersResult = UserMappingFileService
        .getMappings[JiraUserMappingItem, Task](
          MappingDirectory.default.userMappingFilePath
        )
        .runSyncUnsafe()

      jiraUsersResult match {
        case Right(jiraUsers) =>
          jiraUsers.foreach { jiraUser =>
            backlogUsers.exists { backlogUser =>
              backlogUser.getUserId == jiraUser.optDst
                .map(_.value)
                .getOrElse(fail("dst item is not defined"))
            } should be(true)
          }
        case Left(error) =>
          fail(error.toString)
      }

    }
  }

  def testVersion(
      jiraConfig: JiraApiConfiguration,
      backlogConfig: BacklogApiConfiguration
  ): Unit =
    "Version" should "match" in {
      val backlogVersions =
        backlogApi.getVersions(backlogConfig.projectKey).asScala
      val jiraVersions =
        jiraRestApi.versionsAPI.projectVersions(jiraConfig.projectKey).right.get
      jiraVersions.foreach { jiraVersion =>
        val optBacklogVersion =
          backlogVersions.find(backlogVersion => jiraVersion.name == backlogVersion.getName)
        optBacklogVersion.isDefined should be(true)
        for {
          backlogVersion <- optBacklogVersion
        } yield {
          assert(jiraVersion.name == backlogVersion.getName)
          assert(
            jiraVersion.description.orNull == backlogVersion.getDescription
          )
        }
      }
    }

  def testIssueType(backlogConfig: BacklogApiConfiguration): Unit =
    "Issue type" should "match" in {
      val backlogIssueTypes =
        backlogApi.getIssueTypes(backlogConfig.projectKey).asScala
      val jiraIssueTypes = jiraRestApi.issueTypeAPI.allIssueTypes().right.get
      jiraIssueTypes.foreach { jiraIssueType =>
        val backlogIssueType = backlogIssueTypes
          .find(backlogIssueType => jiraIssueType.name == backlogIssueType.getName)
          .get
        jiraIssueType.name should equal(backlogIssueType.getName)
      }
    }

  def testCategory(
      jiraConfig: JiraApiConfiguration,
      backlogConfig: BacklogApiConfiguration
  ): Unit =
    "Category" should "match" in {
      val backlogComponents =
        backlogApi.getCategories(backlogConfig.projectKey).asScala
      val jiraComponents = jiraRestApi.componentAPI
        .projectComponents(jiraConfig.projectKey)
        .right
        .get
      jiraComponents.foreach { jiraComponent =>
        val backlogComponent = backlogComponents
          .find(backlogComponent => jiraComponent.name == backlogComponent.getName)
          .get
        jiraComponent.name should equal(backlogComponent.getName)
      }
    }

  def testCustomFieldDefinitions(backlogConfig: BacklogApiConfiguration): Unit =
    "Custom field definition" should "match" in {
      val backlogCustomFields =
        backlogApi.getCustomFields(backlogConfig.projectKey).asScala
      jiraCustomFieldDefinitions.filter(_.id.contains("customfield_")).foreach { jiraCustomField =>
        val backlogCustomField =
          backlogCustomFields.find(_.getName == jiraCustomField.name).get
        jiraCustomField.name should equal(backlogCustomField.getName)
      }
    }

  def testIssue(backlogConfig: BacklogApiConfiguration): Unit = {

    val backlogProject = backlogApi.getProject(backlogConfig.projectKey)
    val params = new GetIssuesParams(
      List(Long.box(backlogProject.getId)).asJava
    )
    val backlogIssues = backlogApi.getIssues(params).asScala

    def fetchIssues(startAt: Long, maxResults: Long): Unit = {
      val issues = jiraIssueService.issues(startAt, maxResults)
      val maybeSprintCustomField =
        jiraCustomFieldDefinitions.find(_.name == "Sprint")

      issues.foreach { jiraIssue =>
        "Issue" should s"match: ${jiraIssue.id} - ${jiraIssue.summary}" in {

          val maybeBacklogIssue =
            backlogIssues.find(backlogIssue => jiraIssue.summary == backlogIssue.getSummary)

          withClue(s"""
                      |jira subject:${jiraIssue.summary}
          """.stripMargin) {
            maybeBacklogIssue should not be None
          }

          maybeBacklogIssue.map { backlogIssue =>
            val jiraIssueFields =
              IssueFieldConverter.toExportIssueFields(jiraIssue.issueFields)

            // issue type
            jiraIssue.issueType.name should equal(
              backlogIssue.getIssueType.getName
            )

            // category
            jiraIssue.components.map { jiraCategory =>
              backlogIssue.getCategory.asScala
                .find(_.getName == jiraCategory.name) should not be empty
            }

            // version
            jiraIssue.fixVersions.map { jiraVersion =>
              backlogIssue.getVersions.asScala
                .find(_.getName == jiraVersion.name) should not be empty
            }

            // milestone
            for {
              sprintCustomField <- maybeSprintCustomField
            } yield {
              jiraIssueFields.filter(_.id == sprintCustomField.id).map { sprint =>
                val backlogMilestones = backlogIssue.getMilestone.asScala
                sprint.value.asInstanceOf[ArrayFieldValue].values.map { jiraMilestone =>
                  backlogMilestones
                    .find(m => jiraMilestone.value.contains(m.getName)) should not be empty
                }
              }
            }

            // parent issue
            if (jiraIssue.parent.isDefined)
              backlogIssue.getParentIssueId should not be 0
            else backlogIssue.getParentIssueId should equal(0)

            // due date
            dateToOptionDateString(jiraIssue.dueDate) should equal(
              dateToOptionDateString(Option(backlogIssue.getDueDate))
            )

            // priority
            convertPriority(jiraIssue.priority.name) should equal(
              backlogIssue.getPriority.getName
            )

            // status
            withClue(s"""
                        |status:   ${jiraIssue.status.name}
                        |converted:${convertStatus(jiraIssue.status.name)}
                        |""".stripMargin) {
              convertStatus(jiraIssue.status.name) should equal(
                backlogIssue.getStatus.getName
              )
            }

            // assignee
            jiraIssue.assignee.map(assertUser(_, backlogIssue.getAssignee))

            // actual hours
            val spentHours = jiraIssue.timeTrack
              .flatMap(t => t.timeSpentSeconds)
              .map(s =>
                BigDecimal(s / 3600d)
                  .setScale(2, BigDecimal.RoundingMode.HALF_UP)
              )
            val actualHours = Option(backlogIssue.getActualHours).map(s =>
              BigDecimal(s).setScale(2, BigDecimal.RoundingMode.HALF_UP)
            )
            spentHours should equal(actualHours)

            // estimated hours
            val jiraHours = jiraIssue.timeTrack
              .flatMap(t => t.originalEstimateSeconds)
              .map(s =>
                BigDecimal(s / 3600d)
                  .setScale(2, BigDecimal.RoundingMode.HALF_UP)
              )
            val backlogHours = Option(backlogIssue.getEstimatedHours).map(s =>
              BigDecimal(s).setScale(2, BigDecimal.RoundingMode.HALF_UP)
            )
            jiraHours should equal(backlogHours)

            // created user
            convertUser(jiraIssue.creator.identifyKey) should equal(
              backlogIssue.getCreatedUser.getUserId
            )

            // created
            timestampToString(jiraIssue.createdAt) should equal(
              timestampToString(backlogIssue.getCreated)
            )

            // updated user
//            withClue(s"""
//                        |JIRA:   ${timestampToString(jiraIssue.updatedAt)}
//                        |backlog:${timestampToString(backlogUpdated(backlogIssue))}
//            """.stripMargin) {
//              timestampToString(jiraIssue.updatedAt) should be(timestampToString(backlogIssue.getUpdated))
//            }

            // attachment file
            val backlogAttachments = backlogIssue.getAttachments.asScala
            jiraIssue.attachments.map { jiraAttachment =>
              val backlogAttachment =
                backlogAttachments.find(_.getName == jiraAttachment.fileName)
              backlogAttachment should not be empty
            }

            // custom field
            val backlogCustomFields = backlogIssue.getCustomFields.asScala
            val maybeRankCustomField =
              jiraCustomFieldDefinitions.find(_.name == "Rank")
            IssueFieldConverter
              .toExportIssueFields(jiraIssue.issueFields)
              .filterNot { field =>
                maybeSprintCustomField.map(_.id).contains(field.id)
              }
              .filterNot { field =>
                maybeRankCustomField.map(_.id).contains(field.id)
              }
              .map { jiraCustomField =>
                val jiraDefinition = jiraCustomFieldDefinitions
                  .find(_.id == jiraCustomField.id)
                  .get
                val backlogDefinition = backlogCustomFieldDefinitions
                  .find(_.getName == jiraDefinition.name)
                  .get
                val backlogCustomField =
                  backlogCustomFields.find(_.getName == jiraDefinition.name).get

                backlogDefinition.getFieldTypeId match {
                  case BacklogConstantValue.CustomField.MultipleList =>
                    val backlogItems = backlogCustomField
                      .asInstanceOf[MultipleListCustomField]
                      .getValue
                      .asScala
                    jiraCustomField.value
                      .asInstanceOf[ArrayFieldValue]
                      .values
                      .map { jiraValue =>
                        backlogItems.find(
                          _.getName == jiraValue.value
                        ) should not be empty
                      }
                  case BacklogConstantValue.CustomField.Text =>
                    val backlogValue =
                      backlogCustomField.asInstanceOf[TextCustomField]
                    jiraCustomField.value match {
                      case UserFieldValue(v) =>
                        v.identifyKey should equal(backlogValue.getValue)
                      case StringFieldValue(v) =>
                        v should equal(
                          Option(backlogValue.getValue).getOrElse("")
                        )
                    }
                  case BacklogConstantValue.CustomField.TextArea =>
                    val backlogValue =
                      backlogCustomField.asInstanceOf[TextAreaCustomField]
                    jiraCustomField.value
                      .asInstanceOf[StringFieldValue]
                      .value should equal(backlogValue.getValue)
                  case BacklogConstantValue.CustomField.Numeric =>
                    val backlogValue = backlogCustomField
                      .asInstanceOf[NumericCustomField]
                      .getValue
                    jiraCustomField.value
                      .asInstanceOf[NumberFieldValue]
                      .v - backlogValue should equal(0)
                  case BacklogConstantValue.CustomField.Date =>
                    val backlogValue =
                      backlogCustomField.asInstanceOf[DateCustomField].getValue
                    val jiraValue =
                      jiraCustomField.value.asInstanceOf[StringFieldValue]
                    jiraDefinition.schema match {
                      case FieldType.DateTime =>
                        assertDate(jiraValue.value, backlogValue)
                      case FieldType.Date =>
                        assertDateTime(jiraValue.value, backlogValue)
                      case _ =>
                        fail(
                          "Custom field type does not match date or datetime"
                        )
                    }
                  case BacklogConstantValue.CustomField.SingleList =>
                    val backlogValue = backlogCustomField
                      .asInstanceOf[SingleListCustomField]
                      .getValue
                    val jiraValue =
                      jiraCustomField.value.asInstanceOf[OptionFieldValue]
                    jiraValue.value should equal(backlogValue.getName)
                  case BacklogConstantValue.CustomField.CheckBox =>
                    val backlogValues = backlogCustomField
                      .asInstanceOf[CheckBoxCustomField]
                      .getValue
                      .asScala
                    jiraCustomField.value match {
                      case v: ArrayFieldValue =>
                        v.values.map { jiraValue =>
                          backlogValues.find(
                            _.getName == jiraValue.value
                          ) should not be empty
                        }
                      case v: StringFieldValue =>
                        backlogValues.find(
                          _.getName == v.value
                        ) should not be empty
                    }
                  case BacklogConstantValue.CustomField.Radio =>
                    val backlogValue =
                      backlogCustomField.asInstanceOf[RadioCustomField]
                    val jiraValue =
                      jiraCustomField.value.asInstanceOf[OptionFieldValue]
                    jiraValue.value should equal(backlogValue.getValue.getName)
                }
              }

            // ----------------------------------------------------------------
            // comments
            // ----------------------------------------------------------------
            val backlogAllComments = allCommentsOfIssue(backlogIssue.getId)

            // comment
            jiraCommentService
              .issueComments(jiraIssue)
              .filterNot { jiraComment =>
                attachmentCommentPattern.findFirstIn(jiraComment.body).isDefined
              }
              .map { jiraComment =>
                val backlogComment =
                  backlogAllComments.find(_.getContent == jiraComment.body.trim)
                backlogComment should not be empty
                assertUser(
                  jiraComment.author,
                  backlogComment.get.getCreatedUser
                )
                timestampToString(jiraComment.createdAt) should be(
                  timestampToString(backlogComment.get.getCreated)
                )
              }

            // ----- Change log -----
            // Test
            //   - creator is same
            //   - created at is same
            val jiraChangeLogs = jiraIssueService.changeLogs(jiraIssue)
            jiraChangeLogs.map { jiraChangeLog =>
              val backlogChangelog = backlogAllComments.find { backlogComment =>
                timestampToString(
                  backlogComment.getCreated
                ) == timestampToString(jiraChangeLog.createdAt)
              }
              backlogChangelog should not be empty
              assertUser(
                jiraChangeLog.optAuthor.get,
                backlogChangelog.get.getCreatedUser
              )
            }

            // description
            val links = jiraChangeLogs.flatMap(
              _.items
                .filter(_.field == LinkChangeLogItemField)
                .flatMap(_.toDisplayString)
            )
            val linksReplace = "\n\n" + links.mkString("\n")
            val backlogDescription =
              backlogIssue.getDescription.replace(linksReplace, "")
            jiraIssue.description.getOrElse("") should equal(backlogDescription)

          }

        }
      }

      if (issues.nonEmpty) {
        fetchIssues(startAt + maxResults, maxResults)
      }
    }

    fetchIssues(0, 10)
  }

  private def allCommentsOfIssue(issueId: Long): Seq[IssueComment] = {
    val allCount = backlogApi.getIssueCommentCount(issueId)

    def loop(
        optMinId: Option[Long],
        comments: Seq[IssueComment],
        offset: Long
    ): Seq[IssueComment] =
      if (offset < allCount) {
        val queryParams = new QueryParams()
        for { minId <- optMinId } yield {
          queryParams.minId(minId)
        }
        queryParams.count(100)
        queryParams.order(QueryParams.Order.Asc)
        val commentsPart =
          backlogApi.getIssueComments(issueId, queryParams).asScala
        val optLastId = for { lastComment <- commentsPart.lastOption } yield {
          lastComment.getId
        }
        loop(optLastId, comments union commentsPart, offset + 100)
      } else comments

    loop(None, Seq.empty[IssueComment], 0).sortWith((c1, c2) =>
      c1.getCreated.before(c2.getCreated)
    )
  }

}
