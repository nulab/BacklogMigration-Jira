package com.nulabinc.backlog.j2b.formatters

import com.nulabinc.backlog.j2b.jira.domain.mapping.JiraStatusMappingItem
import com.nulabinc.backlog.migration.common.domain.mappings.{Formatter, StatusMapping}

object JiraFormatter {
  implicit object StatusFormatter extends Formatter[StatusMapping[JiraStatusMappingItem]] {
    def format(value: StatusMapping[JiraStatusMappingItem]): (String, String) =
      (value.src.display, value.optDst.map(_.value).getOrElse(""))
  }
}
