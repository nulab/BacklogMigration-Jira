package com.nulabinc.backlog.j2b.jira.domain

import better.files.{File => Path}

import com.nulabinc.backlog.migration.common.utils.IOUtil
import com.nulabinc.jira.client.domain.{Priority, Status, User}

case class CollectData(
  users: Set[User],
  statuses: Seq[Status],
  priorities: Seq[Priority]
) {

  import spray.json._

  def outputJiraUsersToFile(filePath: Path): Unit = {
    import com.nulabinc.jira.client.json.UserMappingJsonProtocol._
    IOUtil.output(filePath, users.toJson.prettyPrint)
  }

  def outputJiraStatusesToFile(filePath: Path): Unit = {
    import com.nulabinc.jira.client.json.StatusMappingJsonProtocol._
    IOUtil.output(filePath, statuses.toJson.prettyPrint)
  }

  def outputJiraPrioritiesToFile(filePath: Path): Unit = {
    import com.nulabinc.jira.client.json.PriorityMappingJsonProtocol._
    IOUtil.output(filePath, priorities.toJson.prettyPrint)
  }
}
