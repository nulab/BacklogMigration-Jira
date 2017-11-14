package com.nulabinc.backlog.j2b.exporter

import com.nulabinc.jira.client.domain.User
import com.nulabinc.jira.client.domain.changeLog._
import org.joda.time.DateTime
import org.specs2.mutable.Specification


class ChangeLogsReconstructorSpec extends Specification {

  val user = User(
    name = "AAA",
    displayName = "AA A"
  )

  val changeLogs = Seq[ChangeLog](
    ChangeLog(
      id = 1,
      author = user,
      createdAt = DateTime.parse("2017-11-13T12:13:27.234+09:00"),
      items = Seq[ChangeLogItem](
        ChangeLogItem(
          field = Component,
          fieldType = ChangeLogItem.FieldType.JIRA,
          fieldId = Some(ComponentFieldId),
          from = None,
          fromDisplayString = None,
          to = Some("10007"),
          toDisplayString = Some("B")
        )
      )
    ),
    ChangeLog(
      id = 2,
      author = user,
      createdAt = DateTime.parse("2017-11-13T12:13:36.154+09:00"),
      items = Seq[ChangeLogItem](
        ChangeLogItem(
          field = Component,
          fieldType = ChangeLogItem.FieldType.JIRA,
          fieldId = Some(ComponentFieldId),
          from = None,
          fromDisplayString = None,
          to = Some("10009"),
          toDisplayString = Some("C")
        )
      )
    ),
    ChangeLog(
      id = 3,
      author = user,
      createdAt = DateTime.parse("2017-11-13T12:13:36.154+09:00"),
      items = Seq[ChangeLogItem](
        ChangeLogItem(
          field = Component,
          fieldType = ChangeLogItem.FieldType.JIRA,
          fieldId = Some(ComponentFieldId),
          from = Some("10006"),
          fromDisplayString = Some("A"),
          to = None,
          toDisplayString = None
        )
      )
    )
  )

  "Calc.run1" >> {
    val init = Seq[String]("B", "C")
    val events = Seq[Event](
      Event(
        from = None,
        to = Some("A")
      ),
      Event(
        from = Some("B"),
        to = None
      )
    )

    val re = Calc.run(init, events)
    re(0) must equalTo(Result(Seq("B", "C"), Seq("B", "C", "A")))
    re(1) must equalTo(Result(Seq("B", "C", "A"), Seq("C", "A")))
  }

  "Calc.run2" >> {
    val init = Seq[String]("A")
    val events = Seq[Event](
      Event(
        from = None,
        to = Some("B")
      ),
      Event(
        from = None,
        to = Some("C")
      ),
      Event(
        from = Some("A"),
        to = None
      )
    )

    val re = Calc.run(init, events)
    println(re)
    re(0) must equalTo(Result(Seq("A"), Seq("A", "B")))
    re(1) must equalTo(Result(Seq("A", "B"), Seq("A", "B", "C")))
    re(2) must equalTo(Result(Seq("A", "B", "C"), Seq("B", "C")))
  }
}
