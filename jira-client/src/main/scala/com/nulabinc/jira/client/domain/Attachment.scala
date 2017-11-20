package com.nulabinc.jira.client.domain

import org.joda.time.DateTime

case class Attachment(
  id: Long,
  fileName: String,
  author: User,
  createdAt: DateTime,
  size: Long,
  mimeType: String,
  content: String
)
