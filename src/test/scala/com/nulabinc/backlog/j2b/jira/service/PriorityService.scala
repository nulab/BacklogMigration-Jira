package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.jira.client.domain.Priority

trait PriorityService {

  def allPriorities(): Seq[Priority]

}
