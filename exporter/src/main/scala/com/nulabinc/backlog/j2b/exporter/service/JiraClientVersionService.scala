package com.nulabinc.backlog.j2b.exporter.service

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.j2b.jira.service.VersionService
import com.nulabinc.jira.client.JiraRestClient

class JiraClientVersionService @Inject()(jira: JiraRestClient,
                                         projectKey: JiraProjectKey) extends VersionService {

  override def all() =
    jira.versionsAPI.projectVersions(projectKey.value).right.get
}
