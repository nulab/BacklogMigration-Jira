package com.nulabinc.backlog.j2b.jira.domain

import com.nulabinc.backlog.j2b.jira.domain.mapping.CustomFieldRow
import com.nulabinc.jira.client.domain.field.Field

case class FieldDefinitions(fields: Seq[Field], definitions: Seq[CustomFieldRow])
