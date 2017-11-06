package com.nulabinc.jira.client.domain

import org.joda.time.DateTime

case class ChangeLog(
  id: Long,
  author: User,
  createdAt: DateTime,
  items: Seq[ChangeLogItem]
)

case class ChangeLogItem(
  field: String,
  fieldType: String,
  fieldId: Option[String],
  from: Option[String],
  to: Option[String]
)

case class ChangeLogResult(
  total: Long,
  isLast: Boolean,
  values: Seq[ChangeLog]
)