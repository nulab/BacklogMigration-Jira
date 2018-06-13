package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.jira.client.domain.Status

trait StatusService {

  def all(): Seq[Status]

}
