package com.nulabinc.backlog.j2b.helper

import java.util.Date

import org.joda.time.DateTime

trait DateFormatter {

  private val dateFormat = "yyyy-MM-dd"

  def dateToOptionDateString(dateTime: Option[Date]): Option[String] =
    dateTime.map(d => new DateTime(d).toString(dateFormat))

}
