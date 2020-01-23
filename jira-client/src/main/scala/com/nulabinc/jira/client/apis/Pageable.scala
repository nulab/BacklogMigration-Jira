package com.nulabinc.jira.client.apis

import io.lemonlabs.uri.Uri
import io.lemonlabs.uri.dsl._

trait Pageable {

  def paginateUri(startAt: Long, maxResults: Long): Uri =
    ("startAt"    -> startAt) &
    ("maxResults" -> maxResults)

}
