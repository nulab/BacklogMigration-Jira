package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.jira.client.domain.Issue

trait IssueReadService {

  def read(filePath: String): Either[IssueIOError, Seq[Issue]]

}
