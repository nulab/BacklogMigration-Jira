package com.nulabinc.backlog.j2b.jira.service

import com.atlassian.jira.rest.client.api.domain.Status

trait StatusService {

  def allStatuses(): Seq[Status]

}
