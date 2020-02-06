package com.nulabinc.backlog.j2b.persistence.store.sqlite.ops

import monix.execution.Scheduler

case class AllTableOps()(implicit exc: Scheduler) {
  val statusMappingTableOps: StatusMappingTableOps = StatusMappingTableOps()
}
