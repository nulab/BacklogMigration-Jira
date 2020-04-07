package com.nulabinc.backlog.j2b.issue.writer

import com.nulabinc.backlog.j2b.issue.writer.convert.ProjectWrites
import com.nulabinc.backlog.j2b.jira.writer.ProjectWriter
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain.BacklogProjectWrapper
import com.nulabinc.backlog.migration.common.utils.IOUtil
import com.nulabinc.jira.client.domain.Project
import javax.inject.Inject
import spray.json._

class ProjectFileWriter @Inject()(implicit val projectWrites: ProjectWrites,
                                  backlogPaths: BacklogPaths) extends ProjectWriter {

  import com.nulabinc.backlog.migration.common.formatters.BacklogJsonProtocol.BacklogProjectWrapperFormat

  override def write(project: Project) = {
    val backlogProject = Convert.toBacklog(project)
    IOUtil.output(backlogPaths.projectJson, BacklogProjectWrapper(backlogProject).toJson.prettyPrint)
    Right(backlogProject)
  }
}
