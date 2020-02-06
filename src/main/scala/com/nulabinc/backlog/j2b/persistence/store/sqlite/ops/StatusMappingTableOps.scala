package com.nulabinc.backlog.j2b.persistence.store.sqlite.ops

import com.nulabinc.backlog.j2b.persistence.store.sqlite.JiraStatusMapping
import com.nulabinc.backlog.j2b.persistence.store.sqlite.tables.JiraStatusMappingTable
import com.nulabinc.backlog.migration.common.persistence.sqlite.ops.BaseTableOps
import monix.execution.Scheduler
import slick.lifted.TableQuery

case class StatusMappingTableOps()(implicit exc: Scheduler) extends BaseTableOps[JiraStatusMapping, JiraStatusMappingTable] {

  protected val tableQuery = TableQuery[JiraStatusMappingTable]

}
