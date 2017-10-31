package com.nulabinc.jira.client.domain.issue

import com.nulabinc.jira.client.domain.User

sealed abstract class FieldValue(val value: String)
case class StringFieldValue(v: String) extends FieldValue(v)
case class NumberFieldValue(v: BigDecimal) extends FieldValue(v.toString)
case class ArrayFieldValue(values: Seq[FieldValue]) extends FieldValue(values.map(_.value).mkString("\n"))
case class OptionFieldValue(v: IssueFieldOption) extends FieldValue(v.value.value)
case class UserFieldValue(v: User) extends FieldValue(v.name.toString)
case class AnyFieldValue(v: String) extends FieldValue(v.toString)

case class IssueField(id: String, value: FieldValue)

case class IssueFieldOption(id: Long, value: FieldValue)


