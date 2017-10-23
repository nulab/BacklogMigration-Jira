package com.nulabinc.backlog.j2b.issue.writer

import javax.inject.Inject

import com.nulabinc.backlog.j2b.issue.writer.convert.IssueTypesWrites
import com.nulabinc.backlog.j2b.jira.writer.IssueTypesWriter
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain.BacklogIssueTypesWrapper
import com.nulabinc.backlog.migration.common.utils.IOUtil
import com.nulabinc.jira.client.domain.IssueType
import spray.json._

class IssueTypesFileWriter @Inject()(implicit val issueTypesWrites: IssueTypesWrites,
                                     backlogPaths: BacklogPaths) extends IssueTypesWriter {

  import com.nulabinc.backlog.migration.common.domain.BacklogJsonProtocol.BacklogIssueTypesWrapperFormat

  override def write(issueTypes: Seq[IssueType]) = {
    val backlogIssueTypes = Convert.toBacklog(issueTypes)
    IOUtil.output(backlogPaths.issueTypesJson, BacklogIssueTypesWrapper(backlogIssueTypes).toJson.prettyPrint)
    Right(backlogIssueTypes)
  }
}
