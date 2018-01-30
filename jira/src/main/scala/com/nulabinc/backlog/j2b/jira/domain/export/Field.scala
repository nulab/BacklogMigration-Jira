package com.nulabinc.backlog.j2b.jira.domain.export

import com.nulabinc.backlog4j.CustomField.{FieldType => BacklogFieldType}

case class Field(
  id: String,
  name: String,
  schema: FieldType
)

sealed abstract class FieldType(
  val value: String,
  val backlogFieldType: BacklogFieldType,
  fieldValueType: FieldValueType = FieldValueType.Single
)

object FieldType {

  // Primitive types
  case object String extends FieldType("string", BacklogFieldType.Text)
  case object Number extends FieldType("number", BacklogFieldType.Numeric)
  case object DateTime extends FieldType("datetime", BacklogFieldType.Date)
  case object Date extends FieldType("date", BacklogFieldType.Date)

  // JIRA specific types
  case object IssueType extends FieldType("issuetype", BacklogFieldType.Text)
  case object Project extends FieldType("project", BacklogFieldType.Text)
  case object FixVersions extends FieldType("fixVersions", BacklogFieldType.MultipleList, FieldValueType.Multi)
  case object Resolution extends FieldType("resolution", BacklogFieldType.Text)
  case object Watches extends FieldType("watches", BacklogFieldType.Text)
  case object Priority extends FieldType("priority", BacklogFieldType.Text)
  case object Labels extends FieldType("labels", BacklogFieldType.MultipleList, FieldValueType.Multi)
  case object Version extends FieldType("version", BacklogFieldType.Text)
  case object Versions extends FieldType("versions", BacklogFieldType.MultipleList, FieldValueType.Multi)
  case object IssueLinks extends FieldType("issuelinks", BacklogFieldType.MultipleList, FieldValueType.Multi)
  case object User extends FieldType("user", BacklogFieldType.Text)
  case object Users extends FieldType("users", BacklogFieldType.MultipleList, FieldValueType.Multi)
  case object Status extends FieldType("status", BacklogFieldType.Text)
  case object Components extends FieldType("components", BacklogFieldType.MultipleList, FieldValueType.Multi)
  case object TimeTracking extends FieldType("timetracking", BacklogFieldType.Text)
  case object Attachments extends FieldType("attachment", BacklogFieldType.MultipleList, FieldValueType.Multi)
  case object WorkLogs extends FieldType("worklog", BacklogFieldType.MultipleList, FieldValueType.Multi)
  case object Group extends FieldType("group", BacklogFieldType.Text)
  case object Groups extends FieldType("group", BacklogFieldType.MultipleList, FieldValueType.Multi)

  // Customizable types
  case object Checkbox extends FieldType("", BacklogFieldType.CheckBox, FieldValueType.Multi)

  def fromString(value: String): FieldType = value match {
    case String.value => String
    case Number.value => Number
    case DateTime.value => DateTime
    case Date.value => Date
    case IssueType.value => IssueType
    case Project.value => Project
    case FixVersions.value => FixVersions
    case Resolution.value => Resolution
    case Watches.value => Watches
  }

  def apply(schemaType: Option[String], schemaSystem: Option[String], schemaItems: Option[String], schemaCustom: Option[String]): FieldType =
    (schemaType, schemaSystem, schemaItems, schemaCustom) match {
      // Check array
      case (Some(FieldValueType.Multi.value), Some(User.value),     _, _) => Users
      case (Some(FieldValueType.Multi.value), Some(Version.value),  _, _) => Versions
      case (Some(FieldValueType.Multi.value), Some(Group.value),    _, _) => Groups
      case (Some(FieldValueType.Multi.value), Some(name),           _, _) => fromString(name)
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
