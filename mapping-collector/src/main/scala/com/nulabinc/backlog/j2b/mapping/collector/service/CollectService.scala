package com.nulabinc.backlog.j2b.mapping.collector.service

import com.nulabinc.jira.client.domain.issue.Issue

trait CollectService[T] {

  def collect(issues: Seq[Issue]): Set[T]
}
