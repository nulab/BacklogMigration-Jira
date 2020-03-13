package com.nulabinc.backlog.j2b.formatters

import com.nulabinc.backlog.j2b.jira.domain.mapping._
import com.nulabinc.backlog.migration.common.domain.mappings._

object JiraFormatter {

  implicit object StatusFormatter extends Formatter[StatusMapping[JiraStatusMappingItem]] {
    def format(value: StatusMapping[JiraStatusMappingItem]): (String, String) =
      (value.src.display, value.optDst.map(_.value).getOrElse(""))
  }

  implicit object PriorityFormatter extends Formatter[PriorityMapping[JiraPriorityMappingItem]] {
    def format(value: PriorityMapping[JiraPriorityMappingItem]): (String, String) =
      (value.src.value, value.optDst.map(_.value).getOrElse(""))
  }

  implicit object UserFormatter extends Formatter[UserMapping[JiraUserMappingItem]] {
    def format(value: UserMapping[JiraUserMappingItem]): (String, String) =
      (value.src.displayName, value.dst.optValue.getOrElse(""))
  }

}
