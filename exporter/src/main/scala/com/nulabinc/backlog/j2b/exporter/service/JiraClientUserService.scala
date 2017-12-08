package com.nulabinc.backlog.j2b.exporter.service

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.service.UserService
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.jira.client.JiraRestClient
import com.nulabinc.jira.client.domain.User

class JiraClientUserService @Inject()(jira: JiraRestClient) extends UserService with Logging {


  override def allUsers() =
    jira.userAPI.users match {
      case Right(users) => users
      case Left(error) => {
        logger.error(error.message)
        Seq.empty[User]
      }
    }

  override def optUserOfKey(key: Option[String]) =
    key.flatMap { k =>
      jira.userAPI.findByKey(k) match {
        case Right(user) => Some(user)
        case Left(error) => {
          logger.error(error.message)
          None
        }
      }
    }

  override def optUserOfName(name: Option[String]) =
    name.flatMap { k =>
      jira.userAPI.findByUsername(k) match {
        case Right(user) => Some(user)
        case Left(error) => {
          logger.error(error.message)
          None
        }
      }
    }
}
