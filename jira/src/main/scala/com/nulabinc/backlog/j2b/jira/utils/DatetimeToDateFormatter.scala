package com.nulabinc.backlog.j2b.jira.utils

import java.text.SimpleDateFormat
import java.util.Locale

import scala.util.Try

trait DatetimeToDateFormatter {

  val readFormats = Seq(
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault()),
    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault())
  )

  val writeFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

  def dateTimeStringToDateString(dateTime: String): String = {
    val date = readFormats.map { readFormat =>
      Try { readFormat.parse(dateTime) }
    }.find(_.isSuccess) match {
      case Some(x) => x.get
      case _       => throw new Exception(s"Can not parse DateTime to Date: $dateTime")
    }
    writeFormat.format(date)
  }

}
