package com.nulabinc.backlog.j2b.mapping.collector.service

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.service.IssueReadService
import com.nulabinc.backlog.j2b.mapping.collector.core._
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.jira.client.domain.{Status, User}

private [collector] class MappingCollector @Inject()(issueReadService: IssueReadService,
                                                     userCollectService: UserCollectService,
                                                     statusCollectService: StatusCollectService)
    extends Logging {

  def boot() = {

    issueReadService.read("issue.txt") match {
      case Right(issues) => {
        val users = userCollectService.collect(issues)
        val statuses = statusCollectService.collect(issues)
        MappingData(users, statuses)
      }
      case Left(error) => {
        logger.error(error.toString)
        MappingData(Set.empty[User], Set.empty[Status])
      }
    }
  }
}
