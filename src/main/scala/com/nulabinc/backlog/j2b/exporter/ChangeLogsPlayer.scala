package com.nulabinc.backlog.j2b.exporter

import com.nulabinc.jira.client.domain.changeLog.{ChangeLog, ChangeLogItemField, ParentChangeLogItemField}

object Calc {

  def run(initialValues: Seq[String], histories: Seq[History]): Seq[Result] = {

    val head = Result(0, Seq.empty[String], to = initialValues)
    val tail = histories.map(history => Result(history.id, from = history.fromToSeq(), to = history.toToSeq()))

    tail
      .foldLeft(Seq(head)) { (a, b) =>
        val prev = Result(a.last.id, a.last.to, Seq.empty[String])

        val r = (b.from, b.to) match {
          case (f, t) if f.isEmpty => prev.copy(b.id, to = a.last.to ++ t)
          case (f, t) if t.isEmpty =>
            prev.copy(
              b.id,
              from = a.last.to,
              to = a.last.to.filterNot(f.contains(_))
            )
          case (f, t) =>
            prev.copy(b.id, to = a.last.to.filterNot(f.contains(_)) ++ t)
        }
        a :+ r
      }
      .tail
  }
}

case class History(id: Long, from: Option[String], to: Option[String]) {

  def fromToSeq(): Seq[String] =
    from match {
      case Some(f) => f.split(",").map(_.trim).toSeq
      case _       => Seq.empty[String]
    }

  def toToSeq(): Seq[String] =
    to match {
      case Some(t) => t.split(",").map(_.trim).toSeq
      case _       => Seq.empty[String]
    }

  def reverse(): History =
    this.copy(from = to, to = from)

}

object History {

  def fromChangeLogs(
      targetField: ChangeLogItemField,
      changeLogs: Seq[ChangeLog]
  ): Seq[History] =
    changeLogs.flatMap { changeLog =>
      changeLog.items.filter(_.field == targetField).map { item =>
        History(changeLog.id, item.fromDisplayString, item.toDisplayString)
      }
    }.distinct

}
case class Result(id: Long, from: Seq[String], to: Seq[String])

object ChangeLogsPlayer {

  def play(
      targetField: ChangeLogItemField,
      latestValues: Seq[String],
      changeLogs: Seq[ChangeLog]
  ): Seq[ChangeLog] = {
    val concatenated = changeLogs.map(concat(targetField, _))
    val histories    = History.fromChangeLogs(targetField, concatenated)
    val result       = Calc.run(latestValues, histories)

    concatenated.map { changeLog =>
      val items = changeLog.items.map { changeLogItem =>
        val r = result.find(_.id == changeLog.id)

        if (changeLogItem.field == targetField) {
          changeLogItem.copy(
            fromDisplayString = r.map(_.from.mkString(",")),
            toDisplayString = r.map(_.to.mkString(","))
          )
        } else
          changeLogItem
      }.distinct
      changeLog.copy(items = items)
    }
  }

  def reversePlay(
      targetField: ChangeLogItemField,
      initialValues: Seq[String],
      changeLogs: Seq[ChangeLog]
  ): Seq[String] = {
    val concatenated = changeLogs.map(concat(targetField, _))
    val histories    = History.fromChangeLogs(targetField, concatenated)
    val result       = Calc.run(initialValues, histories.reverse.map(_.reverse()))

    result.lastOption match {
      case Some(r) => r.to.distinct
      case _       => initialValues
    }
  }

  private def concat(
      targetField: ChangeLogItemField,
      changeLog: ChangeLog
  ): ChangeLog = {

    def makeStrings(array: Seq[String]) = {
      if (array.nonEmpty) Some(array.mkString(","))
      else None
    }

    val fromNames = targetField match {
      case ParentChangeLogItemField =>
        changeLog.items.filter(_.field == targetField).flatten(_.from)
      case _ =>
        changeLog.items.filter(_.field == targetField).flatten(_.fromDisplayString)
    }
    val toNames = targetField match {
      case ParentChangeLogItemField =>
        changeLog.items.filter(_.field == targetField).flatten(_.to)
      case _ =>
        changeLog.items.filter(_.field == targetField).flatten(_.toDisplayString)
    }

    val fromStrings = makeStrings(fromNames)
    val toStrings   = makeStrings(toNames)

    val items = changeLog.items.map { item =>
      if (item.field == targetField)
        item.copy(fromDisplayString = fromStrings, toDisplayString = toStrings)
      else item
    }.distinct
    changeLog.copy(items = items)
  }
}
