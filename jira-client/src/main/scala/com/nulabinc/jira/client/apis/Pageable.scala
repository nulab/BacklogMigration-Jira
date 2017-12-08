package com.nulabinc.jira.client.apis

import com.netaporter.uri.Uri
import com.netaporter.uri.dsl._

trait Pageable {

  def paginateUri(startAt: Long, maxResults: Long): Uri =
    ("startAt"    -> startAt) &
    ("maxResults" -> maxResults)

}
