package com.nulabinc.backlog.j2b.exporter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.jira.writer._

class Exporter @Inject()(projectKey: JiraProjectKey,
                         projectService: ProjectService,
                         projectWriter: ProjectWriter,
                         categoryService: CategoryService,
                         categoryWriter: IssueCategoriesWriter) {

  def export(): Unit = {

    val project = projectService.getProjectByKey(projectKey)
    val categories = categoryService.all()

    for {
      _ <- projectWriter.write(project).right
      _ <- categoryWriter.write(categories).right
    } yield ()


  }
}
