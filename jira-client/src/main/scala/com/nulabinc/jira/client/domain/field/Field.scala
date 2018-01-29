package com.nulabinc.jira.client.domain.field

case class Field(
  id: String,
  name: String,
  schema: FieldType
)

object FieldValueType {
  case object Single extends FieldValueType
  case object Multi extends FieldValueType
}

sealed trait FieldValueType

object FieldType {

  // Primitive types
  case object String extends FieldType("string")
  case object Number extends FieldType("number")
  case object DateTime extends FieldType("datetime")
  case object Date extends FieldType("date")

  // JIRA specific types
  case object IssueType extends FieldType("issuetype")
  case object Project extends FieldType("project")
  case object FixVersions extends FieldType("fixVersions", FieldValueType.Multi)
  case object Resolution extends FieldType("resolution")
  case object Watches extends FieldType("watches")
  case object Priority extends FieldType("priority")
  case object Labels extends FieldType("labels", FieldValueType.Multi)
  case object Versions extends FieldType("versions", FieldValueType.Multi)
  case object IssueLinks extends FieldType("issuelinks", FieldValueType.Multi)
  case object User extends FieldType("user")
  case object Status extends FieldType("status")
  case object Components extends FieldType("components", FieldValueType.Multi)
  case object TimeTracking extends FieldType("timetracking")
  case object Attachments extends FieldType("attachment", FieldValueType.Multi)
  case object WorkLogs extends FieldType("worklog", FieldValueType.Multi)

  def fromString(value: String): FieldType = value match {
    case String.value => String
    case Number.value => Number
    case DateTime.value => DateTime
    case Date.value => Date
    case IssueType.value => IssueType
    case Project.value => Project
    case FixVersions.value => FixVersions
    case Resolution.value => Resolution
  }

  def apply(schemaType: Option[String], schemaSystem: Option[String], schemaItems: Option[String]): FieldType =
    (schemaType, schemaSystem, schemaItems) match {
      case (Some("array"), Some(name), _) => fromString(name)
      case (Some(name), _, _)             => fromString(name)
      case (None, _, _)                   => String
    }
}

sealed abstract class FieldType(
  val value: String,
  fieldValueType: FieldValueType = FieldValueType.Single
)