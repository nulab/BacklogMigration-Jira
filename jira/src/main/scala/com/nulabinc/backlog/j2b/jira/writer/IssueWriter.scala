package com.nulabinc.backlog.j2b.jira.writer

import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.j2b.jira.service.IssueIOError
import com.nulabinc.jira.client.domain.Issue

trait IssueWriter {

  def write(projectKey: JiraProjectKey, filePath: String): Either[IssueIOError, Seq[Issue]]

}
