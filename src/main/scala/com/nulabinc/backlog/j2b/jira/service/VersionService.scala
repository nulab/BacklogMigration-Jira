package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.jira.client.domain.Version

trait VersionService {

  def all(): Seq[Version]

}
