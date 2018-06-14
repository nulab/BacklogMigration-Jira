package com.nulabinc.backlog.j2b.jira.domain

import com.nulabinc.backlog.j2b.jira.domain.export.Field
import com.nulabinc.backlog.j2b.jira.domain.mapping.CustomFieldRow

case class FieldDefinitions(fields: Seq[Field], definitions: Seq[CustomFieldRow])
