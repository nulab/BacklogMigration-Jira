package com.nulabinc.backlog.j2b.issue.writer

import java.util.Date
import javax.inject.Inject

import com.nulabinc.backlog.j2b.issue.writer.convert.IssueWrites
import com.nulabinc.backlog.j2b.jira.writer.IssueWriter
import com.nulabinc.backlog.migration.common.domain.BacklogJsonProtocol._
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.domain.BacklogIssue
import com.nulabinc.backlog.migration.common.utils.IOUtil
import spray.json._

class IssueFileWriter @Inject()(implicit val issueWrites: IssueWrites,
                               backlogPaths: BacklogPaths) extends IssueWriter {

  override def write(backlogIssue: BacklogIssue, issueCreatedAt: Date) = {
    val issueDirPath = backlogPaths.issueDirectoryPath("issue", backlogIssue.id, issueCreatedAt, 0)
    IOUtil.output(backlogPaths.issueJson(issueDirPath), backlogIssue.toJson.prettyPrint)
    Right(backlogIssue)
  }
}
