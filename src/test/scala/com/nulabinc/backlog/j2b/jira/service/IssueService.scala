package com.nulabinc.backlog.j2b.jira.service

import better.files.{File => Path}
import com.nulabinc.jira.client.DownloadResult
import com.nulabinc.jira.client.domain.changeLog.ChangeLog
import com.nulabinc.jira.client.domain.issue.Issue

trait IssueService {

  def count(): Long

  def issues(startAt: Long, maxResults: Long): Seq[Issue]

  def changeLogs(issue: Issue): Seq[ChangeLog]

  def downloadAttachments(attachmentId: Long, destinationPath: Path, fileName: String): DownloadResult
  
}
