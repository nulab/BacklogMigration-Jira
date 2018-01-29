package com.nulabinc.jira.client.domain.field

case class Field(
  id: String,
  name: String,
  schema: FieldType
)

sealed abstract class FieldType(val value: String, fieldValueType: FieldValueType = FieldValueType.Single)

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
  case object Version extends FieldType("version")
  case object Versions extends FieldType("versions", FieldValueType.Multi)
  case object IssueLinks extends FieldType("issuelinks", FieldValueType.Multi)
  case object User extends FieldType("user")
  case object Users extends FieldType("users", FieldValueType.Multi)
  case object Status extends FieldType("status")
  case object Components extends FieldType("components", FieldValueType.Multi)
  case object TimeTracking extends FieldType("timetracking")
  case object Attachments extends FieldType("attachment", FieldValueType.Multi)
  case object WorkLogs extends FieldType("worklog", FieldValueType.Multi)
  case object Group extends FieldType("group")
  case object Groups extends FieldType("group", FieldValueType.Multi)

  // Customizable types
  case object Checkbox extends FieldType("", FieldValueType.Multi)

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

  def apply(schemaType: Option[String], schemaSystem: Option[String], schemaItems: Option[String], schemaCustom: Option[String]): FieldType =
    (schemaType, schemaSystem, schemaItems, schemaCustom) match {
      // Check array
      case (Some(FieldValueType.Multi.value), Some(User.value), _, _)    => Users
      case (Some(FieldValueType.Multi.value), Some(Version.value), _, _) => Versions
      case (Some(FieldValueType.Multi.value), Some(Group.value), _, _)   => Groups
      case (Some(FieldValueType.Multi.value), Some(name), _, _)          => fromString(name)
      // Check customizable
      case (Some(String.value), None, None, Some(custom)) if custom.contains("checkbox") => Checkbox
      // Check single
      case (Some(name), _, _, _) => fromString(name)
      case (None, _, _, _)       => String
    }
}



/*
  Whether a single value or multiple values
 */
sealed abstract class FieldValueType(val value: String)

object FieldValueType {
  case object Single extends FieldValueType("")
  case object Multi extends FieldValueType("array")
}

