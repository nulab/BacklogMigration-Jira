package com.nulabinc.backlog.j2b.exporter

import com.nulabinc.jira.client.domain.changeLog.{ChangeLog, ChangeLogItemField, Component}

object Calc {

  def run(initialValues: Seq[String], events: Seq[Event]): Seq[Result] = {

    val head = Result(Seq.empty[String], to = initialValues)
    val tail = events.map { a =>
      val f = if (a.from.isDefined) a.from.get.split(",").toSeq else Seq.empty[String]
      val t = if (a.to.isDefined) a.to.get.split(",").toSeq else Seq.empty[String]
      Result(from = f, to = t)
    }

    println(tail)
    println("==============")

    tail.foldLeft(Seq(head)) {
      (a, b) =>

        val prev = Result(a.last.to, Seq.empty[String])
        println("Prev:" + prev)

        val r = (b.from, b.to) match {
          case (f, t) if f.isEmpty => prev.copy(to = a.last.to ++ t)
          case (f, t) if t.isEmpty => prev.copy(from = a.last.to, to = a.last.to.filterNot(f.contains(_)))
            // TODO: case (f, t) =>
        }

        println("Result:" + r)
        a :+ r
    }.tail
  }
}

case class Event(from: Option[String], to: Option[String])
case class Result(from: Seq[String], to: Seq[String])


object ChangeLogsReconstructor {

  def reconstruct(latestValues: Seq[String], changeLogs: Seq[ChangeLog]): Seq[ChangeLog] = {
    val category = impl(Component, latestValues, changeLogs)

    category
  }

  private def impl(targetField: ChangeLogItemField, lastValues: Seq[String], changeLogs: Seq[ChangeLog]): Seq[ChangeLog] = {
    val concated = changeLogs.map(concat(targetField, _))

    val events = concated.flatMap { changeLog =>
      changeLog.items.filter(_.field == targetField).map { item => Event(item.fromDisplayString, item.toDisplayString)}
    }

    val result = Calc.run(lastValues, events)

    val ret = result.zip(concated).map {
      case (r, changeLog) =>
        val items = changeLog.items.map { changeLogItem =>
          if (changeLogItem.field == targetField) changeLogItem.copy(fromDisplayString = Some(r.from.mkString(", ")), toDisplayString = Some(r.to.mkString(", ")))
          else changeLogItem
        }.distinct
        changeLog.copy(items = items)
    }
    ret
  }

  private def concat(targetField: ChangeLogItemField, changeLog: ChangeLog): ChangeLog = {

    def makeStrings(array: Seq[String]) = {
      if (array.nonEmpty) Some(array.mkString(", "))
      else None
    }

    val fromNames = changeLog.items.filter(_.field == Component).flatten(_.fromDisplayString)
    val toNames   = changeLog.items.filter(_.field == Component).flatten(_.toDisplayString)

    val fromStrings = makeStrings(fromNames)
    val toStrings = makeStrings(toNames)

    val items = changeLog.items.map { item =>
      if (item.field == targetField) item.copy(fromDisplayString = fromStrings, toDisplayString = toStrings)
      else item
    }.distinct
    changeLog.copy(items = items)
  }
}
