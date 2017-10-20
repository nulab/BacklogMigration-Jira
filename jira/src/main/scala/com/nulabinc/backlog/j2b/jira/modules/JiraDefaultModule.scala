package com.nulabinc.backlog.j2b.jira.modules

import com.google.inject.AbstractModule
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.jira.client.JiraRestClient
import com.nulabinc.jira.client.domain.Project

class JiraDefaultModule(apiConfig: JiraApiConfiguration) extends AbstractModule {

  override def configure() = {

    val jira = JiraRestClient(
      apiConfig.url,
      apiConfig.username,
      apiConfig.password
    )
    val project = jira.projectRestClient.project(apiConfig.projectKey).right.get

    bind(classOf[JiraRestClient]).toInstance(jira)
    bind(classOf[Project]).toInstance(project)
    bind(classOf[JiraApiConfiguration]).toInstance(apiConfig)
//    bind(classOf[PropertyValue]).toInstance(createPropertyValue(redmine, project))
//    bind(classOf[RedmineProjectId]).toInstance(RedmineProjectId(project.getId))
  }
}
