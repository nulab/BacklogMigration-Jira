package com.nulabinc.jira.client.domain

import org.joda.time.DateTime

case class Version(
  id: Option[Long],
  name: String,
  description: String,
  archived: Boolean,
  released: Boolean,
  releaseDate: Option[DateTime]
)
