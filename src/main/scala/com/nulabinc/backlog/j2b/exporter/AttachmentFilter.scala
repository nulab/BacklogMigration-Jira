package com.nulabinc.backlog.j2b.exporter

import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.issue.Issue

import scala.util.matching.Regex

object AttachmentFilter {

  val fileNamePattern: Regex = """\[\^(.+?)\]""".r

  def filteredIssue(issue: Issue, comments: Seq[Comment]): Issue = {
    val issueAttachments = issue.attachments
    val fileNames = extractFileNameFromComments(comments)
    val filteredAttachments = issueAttachments.filterNot(attachment =>
      fileNames.contains(attachment.fileName)
    )

    issue.copy(attachments = filteredAttachments)
  }

  private[exporter] def extractFileNameFromComments(
      comments: Seq[Comment]
  ): Seq[String] =
    comments.flatMap { comment =>
      fileNamePattern.findAllMatchIn(comment.body).map(m => m.group(1))
    }

  private[exporter] def findComment(comments: Seq[Comment]): Seq[Comment] =
    comments.filter { comment =>
      fileNamePattern.findAllMatchIn(comment.body).nonEmpty
    }
}
