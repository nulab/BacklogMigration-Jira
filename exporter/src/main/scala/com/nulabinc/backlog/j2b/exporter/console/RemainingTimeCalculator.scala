package com.nulabinc.backlog.j2b.exporter.console

import java.util.Date

import com.nulabinc.backlog.migration.common.utils.{ConsoleOut, DateUtil, Logging, ProgressBar}
import com.osinka.i18n.Messages
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.ansi

class RemainingTimeCalculator(totalSize: Long) extends Logging {

  private var failed    = 0
  private var date      = ""

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

  def progress(indexOfDate: Int, totalOfDate: Int, summary: String): Unit = {
    newLine = indexOfDate == 1
    clear()
    remainingTime = remainingTime.action()
    val message = remaining()
    ConsoleOut.outStream.println(message)
    isMessageMode = false
  }

  private def clear(): Unit = {
    if (newLine && !isMessageMode) {
      ConsoleOut.outStream.println()
    }
    (0 until 3).foreach { _ =>
      ConsoleOut.outStream.print(ansi.cursorLeft(999).cursorUp(1).eraseLine(Ansi.Erase.ALL))
    }
    ConsoleOut.outStream.flush()
    newLine = false
  }

  private def remaining(): String = {
    val progressBar = ProgressBar.progressBar(remainingTime.count.toInt, remainingTime.totalSize.toInt)
    val time        = Messages("export.remaining_time", DateUtil.timeFormat(new Date(remainingTime.remainingTime)))
    s"$progressBar $time"
  }
}
