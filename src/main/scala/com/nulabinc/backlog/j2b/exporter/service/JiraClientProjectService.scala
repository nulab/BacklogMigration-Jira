package com.nulabinc.backlog.j2b.exporter.service

import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.j2b.jira.service.ProjectService
import com.nulabinc.jira.client.JiraRestClient
import javax.inject.Inject

class JiraClientProjectService @Inject()(jiraRestClient: JiraRestClient) extends ProjectService {

  override def getProjectByKey(projectKey: JiraProjectKey) =
    jiraRestClient.projectAPI.project(projectKey.value).right.get
}
