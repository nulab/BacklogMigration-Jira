package com.nulabinc.backlog.j2b.persistence.store.ops

import com.nulabinc.backlog.migration.common.persistence.store.sqlite.ops.BaseTableOps
import com.nulabinc.jira.client.domain.Status
import doobie._
import doobie.implicits._

object JiraStatusOps extends BaseTableOps {

  def find(id: String): Query0[Status] =
    sql"""
      select * from jira_statuses where id = $id
    """.query[Status]

  def getAll: Query0[Status] =
    sql"select * from jira_statuses".query[Status]

  def store(statuses: Seq[Status]): ConnectionIO[Int] = {
    import cats.implicits._

    Update[Status](
      """
        insert into jira_statuses
          (id, name)
        values
          (?, ?)
        """
    ).updateMany(statuses.toList)
  }

  def createTable(): Update0 =
    sql"""
      create table jira_statuses (
        id    text not null primary key,
        name  text not null
      )
    """.update
}
