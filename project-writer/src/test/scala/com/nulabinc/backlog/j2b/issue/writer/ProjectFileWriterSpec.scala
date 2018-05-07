package com.nulabinc.backlog.j2b.issue.writer

import com.nulabinc.backlog.migration.common.convert.BacklogUnmarshaller
import com.nulabinc.jira.client.domain.Project
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class ProjectFileWriterSpec extends Specification with FileWriterTestHelper with Mockito {

  if (paths.projectJson.exists) paths.projectJson.delete()

  "should write a project to file" >> {

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
