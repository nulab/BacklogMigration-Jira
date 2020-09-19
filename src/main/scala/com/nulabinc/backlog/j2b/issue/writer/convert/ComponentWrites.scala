package com.nulabinc.backlog.j2b.issue.writer.convert

import javax.inject.Inject

import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain.BacklogIssueCategory
import com.nulabinc.jira.client.domain.Component

private[writer] class ComponentWrites @Inject() () extends Writes[Seq[Component], Seq[BacklogIssueCategory]] {

  override def writes(categories: Seq[Component]): Seq[BacklogIssueCategory] =
    categories.map(toBacklog)

  private[this] def toBacklog(category: Component) =
    BacklogIssueCategory(
      optId = Some(category.id),
      name = category.name,
      delete = false
    )

}
