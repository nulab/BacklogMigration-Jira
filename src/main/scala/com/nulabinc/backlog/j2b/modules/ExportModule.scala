package com.nulabinc.backlog.j2b.modules

import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.exporter.service._
import com.nulabinc.backlog.j2b.issue.writer._
import com.nulabinc.backlog.j2b.issue.writer.convert.{IssueFieldWrites, UserWrites}
import com.nulabinc.backlog.j2b.jira.service._
import com.nulabinc.backlog.j2b.jira.writer._

class ExportModule(config: AppConfiguration) extends DefaultModule(config) {

  override def configure(): Unit = {
    super.configure()

    // Data
    val fields = jira.fieldAPI.all().right.get

    // Writes
    bind(classOf[UserWrites]).toInstance(new UserWrites)
    bind(classOf[IssueFieldWrites]).toInstance(new IssueFieldWrites(fields))

    // Writer
    bind(classOf[ProjectWriter]).to(classOf[ProjectFileWriter])
    bind(classOf[ComponentWriter]).to(classOf[ComponentFileWriter])
    bind(classOf[VersionWriter]).to(classOf[VersionFileWriter])
    bind(classOf[IssueTypeWriter]).to(classOf[IssueTypeFileWriter])
    bind(classOf[FieldWriter]).to(classOf[FieldFileWriter])
    bind(classOf[IssueWriter]).to(classOf[IssueFileWriter])

    // Exporter
    bind(classOf[ProjectService]).to(classOf[JiraClientProjectService])
    bind(classOf[CategoryService]).to(classOf[JiraClientCategoryService])
    bind(classOf[VersionService]).to(classOf[JiraClientVersionService])
    bind(classOf[IssueTypeService]).to(classOf[JiraClientIssueTypeService])
    bind(classOf[FieldService]).to(classOf[JiraClientFieldService])
    bind(classOf[StatusService]).to(classOf[JiraClientStatusService])
    bind(classOf[IssueService]).to(classOf[JiraClientIssueService])
    bind(classOf[PriorityService]).to(classOf[JiraClientPriorityService])
  }


}
