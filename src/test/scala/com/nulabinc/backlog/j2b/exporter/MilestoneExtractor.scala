package com.nulabinc.backlog.j2b.exporter

import com.nulabinc.backlog.j2b.jira.domain.export.{ArrayFieldValue, Field, IssueField, Milestone}

object MilestoneExtractor {

  def extract(fieldDefinitions: Seq[Field], issueFields: Seq[IssueField]): Seq[Milestone] = {
    fieldDefinitions.find(_.name == "Sprint") match {
      case Some(sprintDefinition) => issueFields.find(_.id == sprintDefinition.id) match {
        case Some(IssueField(_, ArrayFieldValue(values))) => values.map(v => Milestone(v.value))
        case _ => Seq.empty[Milestone]
      }
      case None => Seq.empty[Milestone]
    }


  }
}
