package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.jira.client.domain.Issue

trait IssueWriteService {

  def write(projectKey: JiraProjectKey, filePath: String): Either[IssueIOError, Seq[Issue]]
}
