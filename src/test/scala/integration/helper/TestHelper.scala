package integration.helper

import java.io.{File, FileInputStream}
import java.util.{Date, Properties}

import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.exporter.service.{
  JiraClientCommentService,
  JiraClientIssueService
}
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.j2b.jira.domain.mapping.{
  JiraPriorityMappingItem,
  JiraStatusMappingItem,
  JiraUserMappingItem,
  ValidatedJiraPriorityMapping,
  ValidatedJiraStatusMapping,
  ValidatedJiraUserMapping
}
import com.nulabinc.backlog.j2b.mapping.collector.MappingCollectDatabaseInMemory
import com.nulabinc.backlog.j2b.mapping.converter.{
  MappingPriorityConverter,
  MappingStatusConverter,
  MappingUserConverter
}
import com.nulabinc.backlog.j2b.mapping.converter.writes.UserWrites
import com.nulabinc.backlog.j2b.mapping.core.MappingDirectory
import com.nulabinc.backlog.migration.common.conf.BacklogApiConfiguration
import com.nulabinc.backlog.migration.common.dsl.{
  AppDSL,
  ConsoleDSL,
  StorageDSL
}
import com.nulabinc.backlog.migration.common.interpreters.{
  JansiConsoleDSL,
  LocalStorageDSL,
  TaskAppDSL
}
import com.nulabinc.backlog.migration.common.modules.{
  ServiceInjector => BacklogInjector
}
import com.nulabinc.backlog.migration.common.service.{
  PriorityService => BacklogPriorityService,
  StatusService => BacklogStatusService,
  UserService => BacklogUserService
}
import com.nulabinc.backlog.migration.common.services.{
  PriorityMappingFileService,
  StatusMappingFileService,
  UserMappingFileService
}
import com.nulabinc.backlog4j.conf.{BacklogConfigure, BacklogPackageConfigure}
import com.nulabinc.backlog4j.{
  BacklogClient,
  BacklogClientFactory,
  Issue => BacklogIssue
}
import com.nulabinc.jira.client.JiraRestClient
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global

import scala.collection.JavaConverters._
import scala.util.matching.Regex

trait TestHelper {
  import com.nulabinc.backlog.j2b.deserializers.JiraMappingDeserializer._
  import com.nulabinc.backlog.migration.common.shared.syntax._

  implicit val appDSL: AppDSL[Task] = TaskAppDSL()
  implicit val storageDSL: StorageDSL[Task] = LocalStorageDSL()
  implicit val consoleDSL: ConsoleDSL[Task] = JansiConsoleDSL()

  val appConfig: AppConfiguration = getAppConfiguration
  val jiraRestApi: JiraRestClient = createJiraRestApi(appConfig.jiraConfig)
  val backlogApi: BacklogClient = createBacklogApi(appConfig.backlogConfig)

  // Backlog services
  val backlogInjector = BacklogInjector.createInjector(appConfig.backlogConfig)
  val backlogUserService =
    backlogInjector.getInstance(classOf[BacklogUserService])
  val backlogPriorityService =
    backlogInjector.getInstance(classOf[BacklogPriorityService])
  val backlogStatusService =
    backlogInjector.getInstance(classOf[BacklogStatusService])

  // Backlog items
  val priorities = backlogPriorityService.allPriorities()
  val statuses = backlogStatusService.allStatuses()
  val users = backlogUserService.allUsers()

  // Mappings
  val priorityMappings = PriorityMappingFileService
    .getMappings[JiraPriorityMappingItem, Task](
      path =
        MappingDirectory.default.priorityMappingFilePath
    )
    .runSyncUnsafe()
    .orFail
  val statusMappings = StatusMappingFileService
    .getMappings[JiraStatusMappingItem, Task](
      path =
        MappingDirectory.default.statusMappingFilePath
    )
    .runSyncUnsafe()
    .orFail
  val userMappings = UserMappingFileService
    .getMappings[JiraUserMappingItem, Task](
      path = MappingDirectory.default.userMappingFilePath
    )
    .runSyncUnsafe()
    .orFail

  val validatedPriorityMappings = PriorityMappingFileService
    .validateMappings(priorityMappings, priorities)
    .orFail
    .map(ValidatedJiraPriorityMapping.from)
  val validatedStatusMappings = StatusMappingFileService
    .validateMappings(statusMappings, statuses)
    .orFail
    .map(ValidatedJiraStatusMapping.from)
  val validatedUserMappings = UserMappingFileService
    .validateMappings(userMappings, users)
    .orFail
    .map(ValidatedJiraUserMapping.from)

  // Mapping converter
  implicit val userWrites = new UserWrites

  // Mapping database
  val database = new MappingCollectDatabaseInMemory

  // JIRA client service
  val jiraCommentService = new JiraClientCommentService(jiraRestApi)
  val jiraIssueService =
    new JiraClientIssueService(JiraProjectKey(appConfig.jiraKey), jiraRestApi)

  // Regex
  val attachmentCommentPattern: Regex = """\[\^.+?\]""".r

  def createJiraRestApi(config: JiraApiConfiguration) =
    new JiraRestClient(
      url = config.url,
      username = config.username,
      apiKey = config.apiKey
    )

  def createBacklogApi(config: BacklogApiConfiguration): BacklogClient = {
    val backlogPackageConfigure: BacklogPackageConfigure =
      new BacklogPackageConfigure(config.url)
    val configure: BacklogConfigure = backlogPackageConfigure.apiKey(config.key)
    new BacklogClientFactory(configure).newClient()
  }

  def convertUser(target: String): String =
    MappingUserConverter.convert(validatedUserMappings, target)

  def convertStatus(target: String): String =
    MappingStatusConverter.convert(validatedStatusMappings, target).name.trimmed

  def convertPriority(target: String): String =
    MappingPriorityConverter.convert(validatedPriorityMappings, target)

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
    val jiraApiKey: String = prop.getProperty("jira.apiKey")
    val jiraUrl: String = prop.getProperty("jira.url")
    val backlogKey: String = prop.getProperty("backlog.key")
    val backlogUrl: String = prop.getProperty("backlog.url")
    val projectKey: String = prop.getProperty("projectKey")

    val keys: Array[String] = projectKey.split(":")
    val jira: String = keys(0)
    val backlog: String =
      if (keys.length == 2) keys(1)
      else keys(0).toUpperCase.replaceAll("-", "_")

    new AppConfiguration(
      jiraConfig = JiraApiConfiguration(
        username = jiraUsername,
        apiKey = jiraApiKey,
        url = jiraUrl,
        projectKey = jira
      ),
      backlogConfig = BacklogApiConfiguration(
        url = backlogUrl,
        key = backlogKey,
        projectKey = backlog
      ),
      retryCount = 0
    )
  }
}
