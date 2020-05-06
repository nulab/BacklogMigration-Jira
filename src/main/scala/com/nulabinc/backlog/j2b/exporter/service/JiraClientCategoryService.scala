package com.nulabinc.backlog.j2b.exporter.service

import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.j2b.jira.service.CategoryService
import com.nulabinc.jira.client.JiraRestClient
import javax.inject.Inject

class JiraClientCategoryService @Inject() (
    jira: JiraRestClient,
    projectKey: JiraProjectKey
) extends CategoryService {

  override def all() =
    jira.componentAPI.projectComponents(projectKey.value).right.get

}
