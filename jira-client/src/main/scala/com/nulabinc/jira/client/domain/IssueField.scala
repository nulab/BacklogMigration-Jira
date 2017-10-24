package com.nulabinc.jira.client.domain

sealed trait FieldValue
case class StringFieldValue(value: String) extends FieldValue
case class NumberFieldValue(value: BigDecimal) extends FieldValue
case class ArrayFieldValue(values: Seq[FieldValue]) extends FieldValue
case class OptionFieldValue(value: IssueFieldOption) extends FieldValue
case class UserFieldValue(value: User) extends FieldValue
case class AnyFieldValue(value: String) extends FieldValue

case class IssueField(id: String, value: FieldValue)

case class IssueFieldOption(id: Long, value: FieldValue)


