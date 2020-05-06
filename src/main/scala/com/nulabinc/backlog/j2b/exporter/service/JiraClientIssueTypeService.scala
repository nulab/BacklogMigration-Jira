package com.nulabinc.backlog.j2b.exporter.service

import com.nulabinc.backlog.j2b.jira.service.IssueTypeService
import com.nulabinc.jira.client.JiraRestClient
import javax.inject.Inject

class JiraClientIssueTypeService @Inject() (jira: JiraRestClient)
    extends IssueTypeService {

  override def all() =
    jira.issueTypeAPI.allIssueTypes().right.get
}
