package com.nulabinc.jira.exporter

import com.nulabinc.backlog.j2b.exporter.{Calc, History, Result}
import org.specs2.mutable.Specification

class ChangeLogsPlayerSpec extends Specification {

  "Calc.run1" >> {
    val init = Seq[String]("B", "C")
    val events = Seq[History](
      History(
        id = 1,
        from = None,
        to = Some("A")
      ),
      History(
        id = 2,
        from = Some("B"),
        to = None
      )
    )

    val re = Calc.run(init, events)
    re(0) must equalTo(Result(1, Seq("B", "C"), Seq("B", "C", "A")))
    re(1) must equalTo(Result(2, Seq("B", "C", "A"), Seq("C", "A")))
  }

  "Calc.run2" >> {
    val init = Seq[String]("A")
    val events = Seq[History](
      History(
        id = 1,
        from = None,
        to = Some("B")
      ),
      History(
        id = 2,
        from = None,
        to = Some("C")
      ),
      History(
        id = 3,
        from = Some("A"),
        to = None
      )
    )

    val re = Calc.run(init, events)
    re(0) must equalTo(Result(1, Seq("A"), Seq("A", "B")))
    re(1) must equalTo(Result(2, Seq("A", "B"), Seq("A", "B", "C")))
    re(2) must equalTo(Result(3, Seq("A", "B", "C"), Seq("B", "C")))
  }

  "Calc.run3" >> {
    val init = Seq("確認", "テスト")
    val histories = Seq(
      History(
        id = 1,
        from = Some("確認"),
        to = Some("設計")
      )
    )
    val actual = Calc.run(init, histories)
    actual(0) must equalTo(Result(1, Seq("確認", "テスト"), Seq("テスト", "設計")))

    val last       = Seq("設計", "テスト")
    val histories2 = histories.reverse.map(_.reverse)
    val replay     = Calc.run(last, histories2)

    replay(0).to must equalTo(Seq("テスト", "確認"))
  }

  "Calc.run4" >> {
    val init = Seq("AAA")
    val histories = Seq(
      History(
        id = 1,
        from = None,
        to = Some("CCC")
      ),
      History(
        id = 2,
        from = Some("BBB, CCC"),
        to = None
      )
    )
    val actual = Calc.run(init, histories)
    actual.last must equalTo(Result(2, Seq("AAA", "CCC"), Seq("AAA")))
  }
}
