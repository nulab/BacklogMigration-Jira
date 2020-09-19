package com.nulabinc.backlog.j2b.exporter.service

import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.j2b.jira.service.ProjectService
import com.nulabinc.jira.client.JiraRestClient
import com.nulabinc.jira.client.domain.Project
import javax.inject.Inject
import monix.eval.Task

class JiraClientProjectService @Inject() (jiraRestClient: JiraRestClient) extends ProjectService[Task] {

  override def getProjectByKey(projectKey: JiraProjectKey): Task[Project] =
    Task.eval {
      jiraRestClient.projectAPI.project(projectKey.value).getOrElse(throw new RuntimeException("cannot get a Jira project"))
    }
}
