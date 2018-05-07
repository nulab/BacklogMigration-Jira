package com.nulabinc.jira.client.domain

import java.util.Date

case class Version(
  id: Option[Long],
  name: String,
  description: Option[String],
  archived: Boolean,
  released: Boolean,
  releaseDate: Option[Date]
)
