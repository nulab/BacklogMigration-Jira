package com.nulabinc.backlog.j2b.exporter.console

import java.util.Date

import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, DateUtil, Logging, ProgressBar}
import com.osinka.i18n.Messages
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.Color._
import org.fusesource.jansi.Ansi.ansi

class RemainingTimeCalculator(totalSize: Long) extends Logging {

  var failed    = 0
  var date      = ""

  private case class RemainingTime(totalSize: Long, lastTime: Long = System.currentTimeMillis(), totalElapsedTime: Long = 0, count: Long = 0) {

    def action(): RemainingTime = {
      val elapsedTime: Long = System.currentTimeMillis() - this.lastTime
      this.copy(
        lastTime = System.currentTimeMillis(),
        totalElapsedTime = totalElapsedTime + elapsedTime,
        count = count + 1
      )
    }

    def average: Float = totalElapsedTime.toFloat / count.toFloat

    def remaining: Long = totalSize - count

    def remainingTime: Long = (remaining * average).toLong
  }

  private var remainingTime = RemainingTime(totalSize)

  private[this] var newLine       = false
  private[this] var isMessageMode = false

  def warning(indexOfDate: Int, totalOfDate: Int, value: String) = {
    message(indexOfDate: Int, totalOfDate: Int, value: String, YELLOW)
  }

  def error(indexOfDate: Int, totalOfDate: Int, value: String) = {
    message(indexOfDate: Int, totalOfDate: Int, value: String, RED)
  }

  private[this] def message(indexOfDate: Int, totalOfDate: Int, value: String, color: Ansi.Color): Unit = {
    clear()
    val message =
      s"""${(" " * 11) + ansi().fg(color).a(value.replaceAll("\n", "")).reset().toString}
         |${current(indexOfDate, totalOfDate, value)}
         |--------------------------------------------------
         |${remaining()}""".stripMargin

    ConsoleOut.outStream.println(message)
    isMessageMode = true
  }

  def progress(indexOfDate: Int, totalOfDate: Int, summary: String) = {
    newLine = (indexOfDate == 1)
    clear()
    remainingTime = remainingTime.action()
    val message =
      s"""${current(indexOfDate, totalOfDate, summary)}
         |--------------------------------------------------
         |${remaining()}""".stripMargin
    ConsoleOut.outStream.println(message)
    isMessageMode = false
  }

  private[this] def clear() = {
    if (newLine && !isMessageMode) {
      ConsoleOut.outStream.println()
    }
    (0 until 3).foreach { _ =>
      ConsoleOut.outStream.print(ansi.cursorLeft(999).cursorUp(1).eraseLine(Ansi.Erase.ALL))
    }
    ConsoleOut.outStream.flush()
    newLine = false
  }

  private[this] def current(indexOfDate: Int, totalOfDate: Int, summary: String): String = {
    val progressBar  = ProgressBar.progressBar(indexOfDate, totalOfDate)
    val resultString = if (failed == 0) Messages("common.result_success") else Messages("common.result_failed", failed)
    val result = if (resultString.nonEmpty) {
      if (resultString == Messages("common.result_success"))
        s"[${ansi().fg(GREEN).a(resultString).reset()}]"
      else s"[${ansi().fg(RED).a(resultString).reset()}]"
    } else resultString

    val message =
      Messages("export.date.execute",
        summary,
        Messages("common.issues"),
        if (indexOfDate == totalOfDate) Messages("message.exported") else Messages("message.exporting"))

    s"$progressBar$result $message"
  }

  private[this] def remaining(): String = {
    val progressBar = ProgressBar.progressBar(remainingTime.count.toInt, remainingTime.totalSize.toInt)
    val message     = Messages("export.progress", remainingTime.count, remainingTime.totalSize)
    val time        = Messages("export.remaining_time", DateUtil.timeFormat(new Date(remainingTime.remainingTime)))
    s"$progressBar $message$time"
  }
}
