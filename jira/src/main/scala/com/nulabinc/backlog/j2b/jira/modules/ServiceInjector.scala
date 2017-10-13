package com.nulabinc.backlog.j2b.jira.modules

import com.google.inject.{AbstractModule, Guice, Injector}
import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.jira.service.{StatusService, StatusServiceImpl}
import com.nulabinc.jira.client.JiraRestClient

object ServiceInjector {

  def createInjector(apiConfig: JiraApiConfiguration): Injector = {
    Guice.createInjector(new AbstractModule() {
      override def configure(): Unit = {

        val jira = JiraRestClient(
          apiConfig.url,
          apiConfig.username,
          apiConfig.password
        )

        bind(classOf[JiraRestClient]).toInstance(jira)
//        bind(classOf[PriorityService]).to(classOf[PriorityServiceImpl])
        bind(classOf[StatusService]).to(classOf[StatusServiceImpl])
//        bind(classOf[UserService]).to(classOf[UserServiceImpl])
//        bind(classOf[ProjectService]).to(classOf[ProjectServiceImpl])
      }
    })
  }
}
