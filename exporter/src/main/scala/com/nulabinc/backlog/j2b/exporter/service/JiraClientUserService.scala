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

  override def optUserOfId(id: String) =
    jira.userAPI.user(id) match {
      case Right(user) => Some(user)
      case Left(error) => {
        logger.error(error.message)
        None
      }
    }
}
