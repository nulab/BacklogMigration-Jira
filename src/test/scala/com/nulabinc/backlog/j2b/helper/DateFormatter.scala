package com.nulabinc.backlog.j2b.helper

import java.util.Date

import org.joda.time.DateTime

trait DateFormatter {

  val dateFormat: String      = "yyyy-MM-dd"
  val timestampFormat: String = "yyyy-MM-dd'T'HH:mm:ssZ"

  def dateToOptionDateString(dateTime: Option[Date]): Option[String] =
    dateTime.map(d => new DateTime(d).toString(dateFormat))

  def dateToDateString(date: Date): String =
    new DateTime(date).toString(dateFormat)

  def timestampToString(date: Date): String =
    new DateTime(date).toString(timestampFormat)

}
