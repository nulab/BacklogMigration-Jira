package com.nulabinc.backlog.j2b.jira.utils

import java.text.SimpleDateFormat
import java.util.Locale

trait DatetimeToDateFormatter {

  def dateTimeStringToDateString(dateTime: String): String = {
    val readFormat      = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
    val writeFormat     = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val parsedDateTime  = readFormat.parse(dateTime)
    writeFormat.format(parsedDateTime)
  }

}
