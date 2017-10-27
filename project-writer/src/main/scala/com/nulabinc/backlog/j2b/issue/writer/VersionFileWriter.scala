package com.nulabinc.backlog.j2b.issue.writer

import javax.inject.Inject

import com.nulabinc.backlog.j2b.issue.writer.convert.VersionWrites
import com.nulabinc.backlog.j2b.jira.writer.VersionWriter
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain.BacklogVersionsWrapper
import com.nulabinc.backlog.migration.common.utils.IOUtil
import com.nulabinc.jira.client.domain.Version
import spray.json._

class VersionFileWriter @Inject()(implicit val versionsWrites: VersionWrites,
                                  backlogPaths: BacklogPaths) extends VersionWriter {

  import com.nulabinc.backlog.migration.common.domain.BacklogJsonProtocol.BacklogVersionsWrapperFormat

  override def write(versions: Seq[Version]) = {
    val backlogVersions = Convert.toBacklog(versions)
    IOUtil.output(backlogPaths.versionsJson, BacklogVersionsWrapper(backlogVersions).toJson.prettyPrint)
    Right(backlogVersions)
  }

}
