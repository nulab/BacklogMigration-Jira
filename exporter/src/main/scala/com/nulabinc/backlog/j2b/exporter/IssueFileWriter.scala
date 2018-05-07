package com.nulabinc.backlog.j2b.exporter

import java.util.Date
import javax.inject.Inject

import com.nulabinc.backlog.j2b.issue.writer.convert.IssueWrites
import com.nulabinc.backlog.j2b.jira.service.IssueService
import com.nulabinc.backlog.j2b.jira.writer.IssueWriter
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.domain.BacklogIssue
import com.nulabinc.backlog.migration.common.domain.BacklogJsonProtocol._
import com.nulabinc.backlog.migration.common.utils.IOUtil
import spray.json._

class IssueFileWriter @Inject()(implicit val issueWrites: IssueWrites,
                                backlogPaths: BacklogPaths,
                                issueService: IssueService) extends IssueWriter {

  override def write(backlogIssue: BacklogIssue, issueCreatedAt: Date) = {
    val issueDirPath = backlogPaths.issueDirectoryPath("issue", backlogIssue.id, issueCreatedAt, 0)

    // download
    val dir  = backlogPaths.issueAttachmentDirectoryPath(issueDirPath)
    backlogIssue.attachments.foreach { attachment =>
      IOUtil.createDirectory(dir)
      val path = backlogPaths.issueAttachmentPath(dir, attachment.name)
      attachment.optId.map { id =>
        issueService.downloadAttachments(id, path.path, attachment.name)
      }
    }

    IOUtil.output(backlogPaths.issueJson(issueDirPath), backlogIssue.toJson.prettyPrint)
    Right(backlogIssue)
  }
}
