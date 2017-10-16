package com.nulabinc.backlog.j2b.jira.service

import javax.inject.Inject

import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.jira.client.JiraRestClient
import com.nulabinc.jira.client.domain.Status

class StatusServiceImpl @Inject()(jira: JiraRestClient) extends StatusService with Logging {

  override def allStatuses(): Seq[Status] =
    jira.statusRestClient.statuses match {
      case Right(statuses) => statuses
      case Left(error) => {
        logger.error(error.message)
        Seq.empty[Status]
      }
    }
}
