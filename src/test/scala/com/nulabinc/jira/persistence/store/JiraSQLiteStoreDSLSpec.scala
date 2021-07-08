package com.nulabinc.jira.persistence.store

import com.nulabinc.backlog.j2b.persistence.store.JiraSQLiteStoreDSL
import com.nulabinc.jira.client.domain.Status
import monix.execution.Scheduler
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.must.Matchers

import java.nio.file.Paths

trait TestFixture {
  implicit val sc: Scheduler = monix.execution.Scheduler.global
  private val dbPath = Paths.get("./jira.db")

  val dsl = JiraSQLiteStoreDSL(dbPath)

  def setup(): Unit = {
    dbPath.toFile.delete()
    dsl.createTable.runSyncUnsafe()
  }
}

class JiraSQLiteStoreDSLSpec
    extends AnyFunSuite
    with Matchers
    with TestFixture {
  val status1 = Status("123", "name1")
  val status2 = Status("id2", "name2")
  val statuses = Seq(status1, status2)

  test("store Jira statuses and find") {
    setup()

    dsl.storeJiraStatuses(statuses).runSyncUnsafe() mustBe ()
    dsl.allJiraStatus.runSyncUnsafe() mustBe statuses
    dsl.findJiraStatus("123").runSyncUnsafe() mustBe Some(status1)
  }
}
