package com.nulabinc.backlog.j2b.jira.service

import com.atlassian.jira.rest.client.api.domain.Priority

trait PriorityService {

  def allPriorities(): Seq[Priority]

}
