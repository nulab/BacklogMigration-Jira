package com.nulabinc.backlog.j2b.exporter.service

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.service.CommentService
import com.nulabinc.jira.client.JiraRestClient
import com.nulabinc.jira.client.domain.issue.Issue

class JiraClientCommentService @Inject()(jira: JiraRestClient) extends CommentService {

  override def issueComments(issue: Issue) =
    jira.commentAPI.issueComments(issue.id).right.get

}
