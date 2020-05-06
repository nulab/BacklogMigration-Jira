package com.nulabinc.jira.client.domain

import java.util.Date

case class Comment(
    id: Long,
    body: String,
    author: User,
    createdAt: Date
)

case class CommentResult(startAt: Long, total: Long, comments: Seq[Comment]) {

  def hasPage(currentComments: Long): Boolean = currentComments < total
}
