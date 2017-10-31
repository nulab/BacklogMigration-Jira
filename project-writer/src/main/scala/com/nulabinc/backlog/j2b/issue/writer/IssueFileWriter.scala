package com.nulabinc.backlog.j2b.issue.writer

import javax.inject.Inject

import com.nulabinc.backlog.j2b.issue.writer.convert.IssueWrites
import com.nulabinc.backlog.j2b.jira.writer.IssueWriter
import com.nulabinc.backlog.migration.common.domain.BacklogJsonProtocol._
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.utils.IOUtil
import com.nulabinc.jira.client.domain.issue.Issue
import spray.json._

class IssueFileWriter @Inject()(implicit val issueWrites: IssueWrites,
                               backlogPaths: BacklogPaths) extends IssueWriter {

  override def write(issue: Issue) = {
    val backlogIssue = Convert.toBacklog(issue)
    val issueCreated = issue.createdAt.toDate
    val issueDirPath = backlogPaths.issueDirectoryPath("issue", issue.id, issueCreated, 0)
    IOUtil.output(backlogPaths.issueJson(issueDirPath), backlogIssue.toJson.prettyPrint)
    Right(backlogIssue)
  }
}
