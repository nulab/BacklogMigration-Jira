package com.nulabinc.backlog.j2b.exporter.service

import com.nulabinc.backlog.j2b.jira.service.PriorityService
import com.nulabinc.jira.client.JiraRestClient
import javax.inject.Inject

class JiraClientPriorityService @Inject() (jira: JiraRestClient) extends PriorityService {

  override def allPriorities() =
    jira.priorityAPI.priorities.right.get
}
