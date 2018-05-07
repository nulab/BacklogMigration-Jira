package com.nulabinc.backlog.j2b.helper

import java.io.{File, FileInputStream}
import java.util.{Date, Properties}

import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.exporter.service.{JiraClientCommentService, JiraClientIssueService}
import com.nulabinc.backlog.j2b.jira.conf.{JiraApiConfiguration, JiraBacklogPaths}
import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.j2b.mapping.collector.MappingCollectDatabaseInMemory
import com.nulabinc.backlog.j2b.mapping.converter.writes.UserWrites
import com.nulabinc.backlog.j2b.mapping.converter.{MappingPriorityConverter, MappingStatusConverter, MappingUserConverter}
import com.nulabinc.backlog.j2b.mapping.file.MappingFileServiceImpl
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration
import com.nulabinc.backlog4j.{IssueComment, Issue => BacklogIssue}
import com.nulabinc.backlog.migration.common.modules.{ServiceInjector => BacklogInjector}
import com.nulabinc.backlog.migration.common.service.{ProjectService, SpaceService, PriorityService => BacklogPriorityService, StatusService => BacklogStatusService, UserService => BacklogUserService}
import com.nulabinc.backlog4j.{BacklogClient, BacklogClientFactory}
import com.nulabinc.backlog4j.conf.{BacklogConfigure, BacklogPackageConfigure}
import com.nulabinc.jira.client.JiraRestClient

import scala.collection.JavaConverters._
import scala.util.matching.Regex

trait TestHelper {


  val appConfig: AppConfiguration = getAppConfiguration
  val jiraRestApi: JiraRestClient = createJiraRestApi(appConfig.jiraConfig)
  val backlogApi: BacklogClient   = createBacklogApi(appConfig.backlogConfig)

  // Backlog services
  val backlogInjector         = BacklogInjector.createInjector(appConfig.backlogConfig)
  val backlogUserService      = backlogInjector.getInstance(classOf[BacklogUserService])
  val backlogPriorityService  = backlogInjector.getInstance(classOf[BacklogPriorityService])
  val backlogStatusService    = backlogInjector.getInstance(classOf[BacklogStatusService])

  // Mapping file
  val jiraBacklogPaths   = new JiraBacklogPaths(appConfig.backlogConfig.projectKey)
  val mappingFileService = new MappingFileServiceImpl(appConfig.jiraConfig, appConfig.backlogConfig)
  val statusMappingFile   = mappingFileService.createStatusesMappingFileFromJson(jiraBacklogPaths.jiraStatusesJson,  backlogStatusService.allStatuses())
  val priorityMappingFile = mappingFileService.createPrioritiesMappingFileFromJson(jiraBacklogPaths.jiraPrioritiesJson, backlogPriorityService.allPriorities())
  val userMappingFile     = mappingFileService.createUserMappingFileFromJson(jiraBacklogPaths.jiraUsersJson, backlogUserService.allUsers())

  // Mappings
  val priorityMappings = priorityMappingFile.tryUnMarshal()
  val statusMappings   = statusMappingFile.tryUnMarshal()
  val userMappings     = userMappingFile.tryUnMarshal()

  // Mapping converter
  implicit val userWrites = new UserWrites
  val priorityMappingConverter = new MappingPriorityConverter
  val statusMappingConverter   = new MappingStatusConverter
  val userMappingConverter     = new MappingUserConverter()

  // Mapping database
  val database = new MappingCollectDatabaseInMemory
  mappingFileService.usersFromJson(jiraBacklogPaths.jiraUsersJson).foreach { user =>
    database.add(user)
  }

  // JIRA client service
  val jiraCommentService = new JiraClientCommentService(jiraRestApi)
  val jiraIssueService = new JiraClientIssueService(appConfig.jiraConfig, JiraProjectKey(appConfig.jiraKey), jiraRestApi, jiraBacklogPaths)

  // Regex
  val attachmentCommentPattern: Regex = """\[\^.+?\]""".r

  def createJiraRestApi(config: JiraApiConfiguration) = new JiraRestClient(
    url = config.url,
    username = config.username,
    password = config.password
  )

  def createBacklogApi(config: BacklogApiConfiguration): BacklogClient = {
    val backlogPackageConfigure: BacklogPackageConfigure = new BacklogPackageConfigure(config.url)
    val configure: BacklogConfigure                      = backlogPackageConfigure.apiKey(config.key)
    new BacklogClientFactory(configure).newClient()
  }

  def convertUser(target: String): String = {
    userMappingConverter.convert(database, userMappings, target)
  }

  def convertStatus(target: String): String = {
    statusMappingConverter.convert(statusMappings, target)
  }

  def convertPriority(target: String): String = {
    priorityMappingConverter.convert(priorityMappings, target)
  }

  def backlogUpdated(issue: BacklogIssue): Date = {
    val comments = backlogApi.getIssueComments(issue.getId)
    if (comments.isEmpty) issue.getUpdated
    else {
      val comment = comments.asScala.sortWith((c1, c2) => {
        val dt1 = c1.getUpdated
        val dt2 = c2.getUpdated
        dt1.before(dt2)
      })(comments.size() - 1)
      comment.getCreated
    }
  }

  private def getAppConfiguration: AppConfiguration = {
    val file = new File("test.properties")
    if (!file.exists())
      throw new RuntimeException("test.properties not found.")

    val prop: Properties = new Properties()
    prop.load(new FileInputStream(file))
    val jiraUsername: String = prop.getProperty("jira.username")
    val jiraPassword: String = prop.getProperty("jira.password")
    val jiraUrl: String      = prop.getProperty("jira.url")
    val backlogKey: String   = prop.getProperty("backlog.key")
    val backlogUrl: String   = prop.getProperty("backlog.url")
    val projectKey: String   = prop.getProperty("projectKey")

    val keys: Array[String] = projectKey.split(":")
    val jira: String        = keys(0)
    val backlog: String     = if (keys.length == 2) keys(1) else keys(0).toUpperCase.replaceAll("-", "_")

    new AppConfiguration(
      jiraConfig = JiraApiConfiguration(
        username = jiraUsername,
        password = jiraPassword,
        url = jiraUrl,
        projectKey = jira
      ),
      backlogConfig = BacklogApiConfiguration(
        url = backlogUrl,
        key = backlogKey,
        projectKey = backlog
      ),
      optOut = true
    )
  }
}
