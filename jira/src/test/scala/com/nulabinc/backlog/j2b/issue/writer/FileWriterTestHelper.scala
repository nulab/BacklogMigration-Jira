package com.nulabinc.backlog.j2b.issue.writer

import java.nio.file.Paths

import com.nulabinc.backlog.j2b.issue.writer.convert._
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.domain.BacklogProjectKey


trait FileWriterTestHelper {

  val projectKey = new BacklogProjectKey("PLAYCMS")

  implicit val projectWrites          = new ProjectWrites(projectKey)
  implicit val issueCategoriesWrites  = new ComponentWrites
  implicit val issueTypesWrites       = new IssueTypeWrites
  implicit val versionsWrites         = new VersionWrites

  implicit val paths                  = new BacklogPaths(projectKey.value, Paths.get("project-writer/target"))
}
