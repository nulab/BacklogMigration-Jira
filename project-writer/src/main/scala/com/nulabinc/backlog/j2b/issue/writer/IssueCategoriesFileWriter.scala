package com.nulabinc.backlog.j2b.issue.writer

import javax.inject.Inject

import com.nulabinc.backlog.j2b.issue.writer.convert.IssueCategoriesWrites
import com.nulabinc.backlog.j2b.jira.writer.IssueCategoriesWriter
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain.BacklogIssueCategoriesWrapper
import com.nulabinc.backlog.migration.common.utils.IOUtil
import com.nulabinc.jira.client.domain.Component
import spray.json._

class IssueCategoriesFileWriter @Inject()(implicit val issueCategoriesWrites: IssueCategoriesWrites,
                                          backlogPaths: BacklogPaths) extends IssueCategoriesWriter {

  import com.nulabinc.backlog.migration.common.domain.BacklogJsonProtocol.BacklogIssueCategoriesWrapperFormat

  override def write(categories: Seq[Component]) = {
    val backlogCategories = Convert.toBacklog(categories)
    IOUtil.output(backlogPaths.issueCategoriesJson, BacklogIssueCategoriesWrapper(Convert.toBacklog(categories)).toJson.prettyPrint)
    Right(backlogCategories)
  }
}
