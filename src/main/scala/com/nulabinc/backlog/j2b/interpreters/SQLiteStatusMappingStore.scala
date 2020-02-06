package com.nulabinc.backlog.j2b.interpreters

import com.nulabinc.backlog.j2b.persistence.store.sqlite.ops.AllTableOps
import com.nulabinc.backlog.j2b.persistence.store.sqlite.{JiraStatusMapping, JiraStatusMappingItem}
import com.nulabinc.backlog.migration.common.domain.Id
import com.nulabinc.backlog.migration.common.domain.mappings.StatusMapping
import com.nulabinc.backlog.migration.common.dsl.{StatusMappingQuery, StatusMappingStoreDSL, StoreDSL}
import com.nulabinc.backlog.migration.common.persistence.sqlite.DBIOTypes.{DBIORead, DBIOWrite}
import monix.eval.Task
import monix.execution.Scheduler

case class SQLiteStatusMappingStore(storeDSL: StoreDSL[Task])
                                   (implicit exc: Scheduler) extends StatusMappingStoreDSL[Task, JiraStatusMappingItem] {

  private val allTableOps = AllTableOps()

  import allTableOps.statusMappingTableOps

  implicit object JiraStatusMappingQuery extends StatusMappingQuery[JiraStatusMappingItem] {
    override def findQuery(mapping: StatusMapping[JiraStatusMappingItem]): DBIORead[Option[JiraStatusMapping]] =
      statusMappingTableOps.select(Id[JiraStatusMapping](mapping.id))

    override def saveQuery(mapping: StatusMapping[JiraStatusMappingItem]): DBIOWrite = {
      val value = new JiraStatusMapping(mapping.id, mapping.optSrc, mapping.optDst)

      statusMappingTableOps.write(value)
    }
  }

  override implicit val mq: StatusMappingQuery[JiraStatusMappingItem] =
    implicitly[StatusMappingQuery[JiraStatusMappingItem]]

}
