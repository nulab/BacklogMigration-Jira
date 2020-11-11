package com.nulabinc.backlog.j2b.mapping

import com.nulabinc.backlog.migration.common.domain.mappings.{
  MappingHeader,
  PriorityMapping,
  StatusMapping,
  UserMapping
}

object JiraMappingHeader {

  implicit object PriorityMappingHeader extends MappingHeader[PriorityMapping[_]] {
    val headers: Seq[String] = Seq("JIRA", "Backlog")
  }

  implicit object StatusMappingHeader extends MappingHeader[StatusMapping[_]] {
    val headers: Seq[String] = Seq("JIRA", "Backlog")
  }

  implicit object UserMappingHeader extends MappingHeader[UserMapping[_]] {
    val headers: Seq[String] = Seq(
      "JIRA Account ID",
      "JIRA display name",
      "Backlog user name",
      "Backlog mapping type"
    )
  }
}
