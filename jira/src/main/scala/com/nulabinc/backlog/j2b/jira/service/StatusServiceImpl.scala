package com.nulabinc.backlog.j2b.jira.service

import javax.inject.Inject

import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.jira.client.JiraRestClient
import com.nulabinc.jira.client.domain.Status

class StatusServiceImpl @Inject()(jira: JiraRestClient) extends StatusService with Logging {

  override def allStatuses(): Seq[Status] = {
    try {
      jira.statusRestClient.statuses
    } catch {
      case e: Throwable =>
        logger.error(e.getMessage, e)
        Seq.empty[Status]
    }
  }
}
