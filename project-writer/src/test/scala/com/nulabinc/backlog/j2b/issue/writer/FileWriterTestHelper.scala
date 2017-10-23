package com.nulabinc.backlog.j2b.issue.writer

import com.nulabinc.backlog.j2b.issue.writer.convert._
import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.migration.common.conf.BacklogPaths

import scalax.file.Path

trait FileWriterTestHelper {

  val projectKey = new JiraProjectKey("PLAYCMS")

  implicit val projectWrites          = new ProjectWrites(projectKey)
  implicit val issueCategoriesWrites  = new IssueCategoriesWrites

  implicit val paths                  = new BacklogPaths(projectKey.value) {
    override def projectJson          = Path.fromString("project-writer/target/project.json")
    override def issueCategoriesJson  = Path.fromString("project-writer/target/categories.json")
  }

  // Delete all previous test results
  paths.projectJson.jfile.delete()
  paths.issueCategoriesJson.jfile.delete()

}
