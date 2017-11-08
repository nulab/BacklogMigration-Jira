package com.nulabinc.jira.client.domain

import org.joda.time.DateTime

case class Comment(
  id: Long,
  body: String,
  author: User,
  createdAt: DateTime
)

case class CommentResult(
  total: Long,
  comments: Seq[Comment]
)