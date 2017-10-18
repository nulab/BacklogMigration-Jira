package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey

trait IssueWriteService {

  def write(projectKey: JiraProjectKey, filePath: String): IssueIOResult
}
