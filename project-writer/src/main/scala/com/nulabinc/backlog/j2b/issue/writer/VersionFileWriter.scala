package com.nulabinc.backlog.j2b.issue.writer

import javax.inject.Inject

import com.nulabinc.backlog.j2b.issue.writer.convert.VersionWrites
import com.nulabinc.backlog.j2b.jira.domain.export.Milestone
import com.nulabinc.backlog.j2b.jira.writer.VersionWriter
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.convert.Convert
import com.nulabinc.backlog.migration.common.domain.BacklogVersionsWrapper
import com.nulabinc.backlog.migration.common.utils.IOUtil
import com.nulabinc.jira.client.domain.Version
import org.joda.time.DateTime
import spray.json._

class VersionFileWriter @Inject()(implicit val versionsWrites: VersionWrites,
                                  backlogPaths: BacklogPaths) extends VersionWriter {

  import com.nulabinc.backlog.migration.common.domain.BacklogJsonProtocol.BacklogVersionsWrapperFormat

  override def write(versions: Seq[Version], milestones: Seq[Milestone]) = {
    val convertedMilestones = milestones.map { milestone =>
      Version(
        id = None,
        name = milestone.name,
        description = milestone.goal,
        archived = false,
        released = false,
        releaseDate = milestone.endDate.map(new DateTime(_))
      )
    }
    val backlogVersions = Convert.toBacklog(versions ++ convertedMilestones)
    IOUtil.output(backlogPaths.versionsJson, BacklogVersionsWrapper(backlogVersions).toJson.prettyPrint)
    Right(backlogVersions)
  }

}
