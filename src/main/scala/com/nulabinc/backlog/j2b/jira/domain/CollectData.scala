package com.nulabinc.backlog.j2b.jira.domain

import better.files.{File => Path}
import com.nulabinc.backlog.j2b.jira.domain.`export`.{
  ChangeLogMappingUser,
  ExistingMappingUser,
  MappingUser
}
import com.nulabinc.backlog.migration.common.utils.IOUtil
import com.nulabinc.jira.client.domain.{Priority, Status}

case class CollectData(
    private val users: Set[MappingUser],
    statuses: Seq[Status],
    priorities: Seq[Priority]
) {

  import spray.json._

  def outputJiraUsersToFile(filePath: Path): Unit = {
    import com.nulabinc.backlog.j2b.formatters.SprayJsonFormats._
    IOUtil.output(filePath, getUsers.toJson.prettyPrint)
  }

  def getUsers: Seq[MappingUser] = {
    val existingUsers = users.flatMap {
      case u: ExistingMappingUser => Some(u)
      case _                      => None
    }

    users.toList.distinctBy(_.key).foldLeft(Seq.empty[MappingUser]) { (acc, item) =>
      item match {
        case u: ExistingMappingUser =>
          acc :+ u
        case u: ChangeLogMappingUser =>
          acc :+ existingUsers.find(_.key == u.key).getOrElse(u)
      }
    }
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
