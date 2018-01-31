package com.nulabinc.backlog.j2b.exporter

import com.nulabinc.backlog.j2b.jira.domain.export.{Field, IssueField}

object IssueFieldFilter {

  def filterMilestone(definitions: Seq[Field], issueFields: Seq[IssueField]): Seq[IssueField] =
    definitions.find(_.name == "Sprint") match {
      case Some(sprintDefinition) => issueFields.filterNot(_.id == sprintDefinition.id)
      case None => issueFields
    }
}
