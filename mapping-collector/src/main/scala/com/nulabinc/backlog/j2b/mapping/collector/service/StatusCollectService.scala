package com.nulabinc.backlog.j2b.mapping.collector.service

import com.nulabinc.jira.client.domain.{Issue, Status}

class StatusCollectService extends CollectService[Status] {

  override def collect(issues: Seq[Issue]) = ???

}
