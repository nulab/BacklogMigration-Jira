package com.nulabinc.backlog.j2b.persistence.store

import com.nulabinc.backlog.j2b.persistence.store.ops.JiraStatusOps
import com.nulabinc.backlog.migration.common.interpreters.SQLiteStoreDSL
import com.nulabinc.jira.client.domain.Status
import doobie.implicits._
import monix.eval.Task
import monix.execution.Scheduler

import java.nio.file.Path

case class JiraSQLiteStoreDSL(dbPath: Path)(implicit sc: Scheduler)
    extends SQLiteStoreDSL(dbPath) {

  def findJiraStatus(id: String): Task[Option[Status]] =
    JiraStatusOps.find(id).option.transact(xa)

  def allJiraStatus: Task[Seq[Status]] =
    Task.from(
      JiraStatusOps.getAll.to[Seq].transact(xa)
    )

  def storeJiraStatuses(statuses: Seq[Status]): Task[Unit] =
    JiraStatusOps.store(statuses).transact(xa).map(_ => ())

  override def createTable: Task[Unit] = {
    for {
      _ <- super.createTable
      _ <- JiraStatusOps.createTable().run.transact(xa).map(_ => ())
    } yield ()
  }
}
