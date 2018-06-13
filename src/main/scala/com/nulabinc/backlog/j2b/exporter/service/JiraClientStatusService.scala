package com.nulabinc.backlog.j2b.exporter.service

import com.nulabinc.backlog.j2b.jira.service.StatusService
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.jira.client.JiraRestClient
import com.nulabinc.jira.client.domain.Status
import javax.inject.Inject

class JiraClientStatusService @Inject()(jira: JiraRestClient) extends StatusService with Logging {

  override def all(): Seq[Status] =
    jira.statusAPI.statuses match {
      case Right(statuses) => statuses
      case Left(error) => {
        logger.error(error.message)
        Seq.empty[Status]
      }
    }
}
