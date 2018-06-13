package com.nulabinc.backlog.j2b.issue.writer

import com.nulabinc.backlog.j2b.issue.writer.convert.UserWrites
import com.nulabinc.backlog.j2b.jira.writer.ProjectUserWriter
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.domain.{BacklogProjectUsersWrapper, BacklogUser}
import com.nulabinc.backlog.migration.common.utils.IOUtil
import javax.inject.Inject
import spray.json._

class ProjectUserFileWriter @Inject()(implicit val userWrites: UserWrites,
                                      backlogPaths: BacklogPaths) extends ProjectUserWriter {


  override def write(users: Seq[BacklogUser]) = {
    import com.nulabinc.backlog.migration.common.domain.BacklogJsonProtocol._
    IOUtil.output(backlogPaths.projectUsersJson   , BacklogProjectUsersWrapper(users).toJson.prettyPrint)
    Right(users)
  }
}