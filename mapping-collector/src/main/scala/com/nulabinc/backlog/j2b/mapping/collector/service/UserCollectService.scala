package com.nulabinc.backlog.j2b.mapping.collector.service

import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.jira.client.domain.{Issue, User}

class UserCollectService() extends CollectService[User] with Logging {

  override def collect(issues: Seq[Issue]) = {


    issues.foldLeft(Set.empty[User]) {
      case (acc, issue) => issue.assignee match {
        case Some(user) => acc + user
        case _ => acc
      }
    }

  }
}
