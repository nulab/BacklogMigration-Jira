package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.jira.client.domain.Component

trait CategoryService {

  def all(): Seq[Component]

}
