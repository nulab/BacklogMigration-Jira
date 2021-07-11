package com.nulabinc.backlog.j2b.exporter.service

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.service.PriorityService
import com.nulabinc.jira.client.JiraRestClient

class JiraClientPriorityService @Inject() (jira: JiraRestClient) extends PriorityService {

  override def allPriorities() =
    jira.priorityAPI.priorities.right.get
}
