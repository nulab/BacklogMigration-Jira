package com.nulabinc.backlog.j2b.helper

import java.util.Date


trait DateFormatter {

  val dateFormat: String      = "yyyy-MM-dd"
  val timestampFormat: String = "yyyy-MM-dd'T'HH:mm:ssZ"

  def dateToOptionDateString(dateTime: Option[Date]): Option[String] =
    dateTime.map(d => d.formatted(dateFormat))

  def dateToDateString(date: Date): String =
    date.formatted(dateFormat)

  def timestampToString(date: Date): String =
    date.formatted(timestampFormat)

}
