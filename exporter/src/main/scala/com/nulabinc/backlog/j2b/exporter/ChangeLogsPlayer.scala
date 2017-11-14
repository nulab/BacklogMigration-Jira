package com.nulabinc.backlog.j2b.exporter

import com.nulabinc.jira.client.domain.changeLog.{ChangeLog, ChangeLogItemField, Component}

object Calc {

  def run(initialValues: Seq[String], histories: Seq[History]): Seq[Result] = {

    val head = Result(Seq.empty[String], to = initialValues)
    val tail = histories.map( history => Result(from = history.fromToSeq(), to = history.toToSeq()))

    tail.foldLeft(Seq(head)) { (a, b) =>

      val prev = Result(a.last.to, Seq.empty[String])

      val r = (b.from, b.to) match {
        case (f, t) if f.isEmpty => prev.copy(to = a.last.to ++ t)
        case (f, t) if t.isEmpty => prev.copy(from = a.last.to, to = a.last.to.filterNot(f.contains(_)))
        case (f, t)              => prev.copy(to = a.last.to.filterNot(f.contains(_)) ++ t)
      }
      a :+ r
    }.tail
  }
}

case class History(from: Option[String], to: Option[String]) {

  def fromToSeq(): Seq[String] = from match {
    case Some(f) => f.split(",").toSeq
    case _       => Seq.empty[String]
  }

  def toToSeq(): Seq[String] = to match {
    case Some(t) => t.split(",").toSeq
    case _       => Seq.empty[String]
  }

  def reverse(): History =
    History(from = to, to = from)

}

object History {

  def fromChangeLogs(targetField: ChangeLogItemField, changeLogs: Seq[ChangeLog]): Seq[History] =
    changeLogs.flatMap { changeLog =>
      changeLog.items.filter(_.field == targetField).map { item => History(item.fromDisplayString, item.toDisplayString)}
    }.distinct

}
case class Result(from: Seq[String], to: Seq[String])


object ChangeLogsPlayer {

  def play(latestValues: Seq[String], changeLogs: Seq[ChangeLog]): Seq[ChangeLog] = {
    val category = impl(Component, latestValues, changeLogs)

    category
  }

  def reversePlay(targetField: ChangeLogItemField, initialValues: Seq[String], changeLogs: Seq[ChangeLog]): Seq[String] = {
    val concatenated = changeLogs.map(concat(targetField, _))
    val histories = History.fromChangeLogs(targetField, concatenated)
    val result = Calc.run(initialValues, histories.reverse.map(_.reverse()))

    result.lastOption match {
      case Some(r) => r.to.distinct
      case _       => Seq.empty[String]
    }
  }

  private def impl(targetField: ChangeLogItemField, lastValues: Seq[String], changeLogs: Seq[ChangeLog]): Seq[ChangeLog] = {
    val concatenated = changeLogs.map(concat(targetField, _))

    val histories = concatenated.flatMap { changeLog =>
      changeLog.items.filter(_.field == targetField).map { item => History(item.fromDisplayString, item.toDisplayString)}
    }.distinct

    val result = Calc.run(lastValues, histories)

    val ret = result.zip(concatenated).map {
      case (r, changeLog) =>
        val items = changeLog.items.map { changeLogItem =>
          if (changeLogItem.field == targetField) changeLogItem.copy(fromDisplayString = Some(r.from.mkString(", ")), toDisplayString = Some(r.to.mkString(", ")))
          else                                    changeLogItem
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

    val fromNames = changeLog.items.filter(_.field == targetField).flatten(_.fromDisplayString)
    val toNames   = changeLog.items.filter(_.field == targetField).flatten(_.toDisplayString)

    val fromStrings = makeStrings(fromNames)
    val toStrings = makeStrings(toNames)

    val items = changeLog.items.map { item =>
      if (item.field == targetField) item.copy(fromDisplayString = fromStrings, toDisplayString = toStrings)
      else                           item
    }.distinct
    changeLog.copy(items = items)
  }
}
