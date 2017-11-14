package com.nulabinc.backlog.j2b.exporter

import org.specs2.mutable.Specification

class ChangeLogsReconstructorSpec extends Specification {

  "Calc.run1" >> {
    val init = Seq[String]("B", "C")
    val events = Seq[History](
      History(
        from = None,
        to = Some("A")
      ),
      History(
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
    val events = Seq[History](
      History(
        from = None,
        to = Some("B")
      ),
      History(
        from = None,
        to = Some("C")
      ),
      History(
        from = Some("A"),
        to = None
      )
    )

    val re = Calc.run(init, events)
    re(0) must equalTo(Result(Seq("A"),           Seq("A", "B")))
    re(1) must equalTo(Result(Seq("A", "B"),      Seq("A", "B", "C")))
    re(2) must equalTo(Result(Seq("A", "B", "C"), Seq("B", "C")))
  }

  "Calc.run3" >> {
    val init = Seq("確認", "テスト")
    val histories = Seq(
      History(
        from = Some("確認"),
        to = Some("設計")
      )
    )
    val actual = Calc.run(init, histories)
    println(actual)
    actual(0) must equalTo(Result(Seq("確認", "テスト"), Seq("テスト", "設計")))
  }
}
