package com.nulabinc.backlog.j2b.helper

import java.text.SimpleDateFormat
import java.util.Date


trait DateFormatter {

  val dateFormat: String      = "yyyy-MM-dd"
  val timestampFormat: String = "yyyy-MM-dd'T'HH:mm:ssZ"

  def dateToOptionDateString(dateTime: Option[Date]): Option[String] =
    dateTime.map(dateToDateString)

  def dateToDateString(date: Date): String =
    new SimpleDateFormat(dateFormat).format(date)

  def timestampToString(date: Date): String =
    new SimpleDateFormat(timestampFormat).format(date)

}
