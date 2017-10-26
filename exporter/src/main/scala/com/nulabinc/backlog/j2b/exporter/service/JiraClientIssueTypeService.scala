package com.nulabinc.backlog.j2b.exporter.service

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.service.IssueTypeService
import com.nulabinc.jira.client.JiraRestClient

class JiraClientIssueTypeService @Inject()(jira: JiraRestClient) extends IssueTypeService {

  override def all() =
    jira.issueTypeRestClient.allIssueTypes().right.get
}
