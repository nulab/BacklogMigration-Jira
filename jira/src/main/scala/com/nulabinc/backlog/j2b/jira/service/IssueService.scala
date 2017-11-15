package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.jira.client.domain.{Attachment, Comment}
import com.nulabinc.jira.client.domain.issue.Issue

import scalax.file.Path


trait IssueService {

  def count(): Long

  def issues(startAt: Long, maxResults: Long): Seq[Issue]

  def injectChangeLogsToIssue(issue: Issue): Issue

  def injectAttachmentsToIssue(issue: Issue): Issue

  def downloadAttachments(attachmentId: Long, destinationPath: Path, fileName: String): Unit
}
