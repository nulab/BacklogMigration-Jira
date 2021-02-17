package com.nulabinc.jira.client.apis

import io.lemonlabs.uri.Url
import io.lemonlabs.uri.dsl._

trait Pageable {

  def paginateUri(startAt: Long, maxResults: Long): Url =
    ("startAt"    -> startAt) &
    ("maxResults" -> maxResults)

}
