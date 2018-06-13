package com.nulabinc.backlog.j2b.jira.utils

trait SecondToHourFormatter {

  def secondsToHours(seconds: Int): Float = {
    val formattedText = "%.2f".format(seconds / 3600f)
    formattedText.toFloat
  }

}
