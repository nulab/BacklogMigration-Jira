package com.nulabinc.backlog.j2b.issue.writer

import com.nulabinc.backlog.j2b.issue.writer.convert.ProjectWrites
import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.jira.client.domain.Project
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import scalax.file.Path

class ProjectFileWriterSpec extends Specification with Mockito {

  "should write a project to file" >> {
    val projectKey = new JiraProjectKey("TEST")
    val filePath = "project-writer/target/project.json"

    implicit val projectWriter = new ProjectWrites(projectKey)
    implicit val paths = new BacklogPaths(projectKey.value) {
      override def projectJson = Path.fromString(filePath)
    }

    val writer = new ProjectFileWriter()
    val project = Project(
      id = 1000,
      key = projectKey.value,
      name = "TEST name",
      description = "some project"
    )
    val actual = writer.write(project)

    actual.isRight must beTrue
    Path.fromString(filePath).isFile must beTrue
  }
}
