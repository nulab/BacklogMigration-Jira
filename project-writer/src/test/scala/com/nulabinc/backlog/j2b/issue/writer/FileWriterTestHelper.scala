package com.nulabinc.backlog.j2b.issue.writer

import better.files.{File => Path}
import com.nulabinc.backlog.j2b.issue.writer.convert._
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.domain.BacklogProjectKey


trait FileWriterTestHelper {

  val projectKey = new BacklogProjectKey("PLAYCMS")

  implicit val projectWrites          = new ProjectWrites(projectKey)
  implicit val issueCategoriesWrites  = new ComponentWrites
  implicit val issueTypesWrites       = new IssueTypeWrites
  implicit val versionsWrites         = new VersionWrites

  implicit val paths                  = new BacklogPaths(projectKey.value) {
    override def projectJson          = Path("project-writer/target/project.json")
    override def issueCategoriesJson  = Path("project-writer/target/categories.json")
    override def issueTypesJson       = Path("project-writer/target/issueTypes.json")
    override def versionsJson         = Path("project-writer/target/versions.json")
  }

  // Delete all previous test results
  paths.projectJson.delete()
  paths.issueCategoriesJson.delete()
  paths.issueTypesJson.delete()
  paths.versionsJson.delete()

}
