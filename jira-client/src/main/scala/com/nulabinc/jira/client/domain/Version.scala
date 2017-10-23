package com.nulabinc.jira.client.domain

import java.util.Date

import org.joda.time.DateTime

// 	public Version(
// URI self,
// @Nullable Long id,
// String name,
// String description,
// boolean archived,
// boolean released,
// @Nullable DateTime releaseDate  2017-08-19
// ) {

case class Version(
  id: Option[Long],
  name: String,
  description: String,
  archived: Boolean,
  released: Boolean,
  releaseDate: Option[DateTime]
)
