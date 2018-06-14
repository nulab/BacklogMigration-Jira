package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.jira.client.domain.Comment
import com.nulabinc.jira.client.domain.issue.Issue

trait CommentService {

  def issueComments(issue: Issue): Seq[Comment]

}
