package com.nulabinc.backlog.j2b.issue.writer

import com.nulabinc.backlog.j2b.issue.writer.convert.ProjectWrites
import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.convert.BacklogUnmarshaller
import com.nulabinc.backlog.migration.common.utils.IOUtil
import com.nulabinc.jira.client.domain.Project
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import scalax.file.Path

class ProjectFileWriterSpec extends Specification with Mockito {

  "should write a project to file" >> {
    val projectKey = new JiraProjectKey("PLAYCMS")
    val filePath = "project-writer/target/project.json"

    implicit val projectWrites  = new ProjectWrites(projectKey)
    implicit val paths          = new BacklogPaths(projectKey.value) {
      override def projectJson  = Path.fromString(filePath)
    }

    val project = Project(
      id = 1000,
      key = projectKey.value,
      name = "TEST name",
      description = "some project"
    )

    // Output to file
    new ProjectFileWriter().write(project)

    val actual = BacklogUnmarshaller.project(paths)

    actual.optId.get must beEqualTo(project.id)
    actual.key       must beEqualTo(project.key)
    actual.name      must beEqualTo(project.name)
  }
}
