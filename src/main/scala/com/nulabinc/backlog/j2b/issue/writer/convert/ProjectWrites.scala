package com.nulabinc.backlog.j2b.issue.writer.convert

import javax.inject.Inject

import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain.{
  BacklogProject,
  BacklogProjectKey
}
import com.nulabinc.backlog4j.Project.TextFormattingRule
import com.nulabinc.jira.client.domain.Project

private[writer] class ProjectWrites @Inject() (projectKey: BacklogProjectKey)
    extends Writes[Project, BacklogProject] {

  override def writes(project: Project) =
    BacklogProject(
      optId = Some(project.id),
      name = project.name,
      key = projectKey.value,
      isChartEnabled = true,
      isSubtaskingEnabled = true,
      textFormattingRule = TextFormattingRule.Markdown.getStrValue
    )
}
