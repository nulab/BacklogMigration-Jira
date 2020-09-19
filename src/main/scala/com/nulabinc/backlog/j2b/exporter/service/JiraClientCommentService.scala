package com.nulabinc.backlog.j2b.exporter.service

import com.nulabinc.backlog.j2b.jira.service.CommentService
import com.nulabinc.jira.client.JiraRestClient
import com.nulabinc.jira.client.domain.Comment
import com.nulabinc.jira.client.domain.issue.Issue
import javax.inject.Inject

class JiraClientCommentService @Inject() (jira: JiraRestClient) extends CommentService {

  override def issueComments(issue: Issue): Seq[Comment] =
    fetch(issue, 0, 100, Seq.empty[Comment])

  private def fetch(
      issue: Issue,
      startAt: Long,
      maxResults: Long,
      comments: Seq[Comment]
  ): Seq[Comment] =
    jira.commentAPI.issueComments(issue.id, startAt, maxResults) match {
      case Right(result) =>
        val appendedComments = comments ++ result.comments
        if (result.hasPage(appendedComments.length))
          fetch(issue, startAt + maxResults, maxResults, appendedComments)
        else appendedComments
      case Left(error) =>
        throw new RuntimeException(
          s"Cannot get issue comments: ${error.message}"
        )
    }

}
