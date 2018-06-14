package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.jira.client.domain.Project

trait ProjectService {

  def getProjectByKey(projectKey: JiraProjectKey): Project

}
