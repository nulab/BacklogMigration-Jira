package com.nulabinc.backlog.j2b.jira.domain.export

import com.nulabinc.backlog4j.CustomField.{FieldType => BacklogFieldType}

case class Field(
    id: String,
    name: String,
    schema: FieldType
)

sealed abstract class FieldType(
    val value: String,
    val backlogFieldType: BacklogFieldType
)

object FieldType {

  // Primitive types
  case object String   extends FieldType("string", BacklogFieldType.Text)
  case object Number   extends FieldType("number", BacklogFieldType.Numeric)
  case object DateTime extends FieldType("datetime", BacklogFieldType.Date)
  case object Date     extends FieldType("date", BacklogFieldType.Date)

  case object Strings extends FieldType("string", BacklogFieldType.MultipleList)

  // JIRA specific types
  case object IssueType       extends FieldType("issuetype", BacklogFieldType.Text)
  case object Project         extends FieldType("project", BacklogFieldType.Text)
  case object FixVersions     extends FieldType("fixVersions", BacklogFieldType.MultipleList)
  case object Resolution      extends FieldType("resolution", BacklogFieldType.Text)
  case object Priority        extends FieldType("priority", BacklogFieldType.Text)
  case object Labels          extends FieldType("labels", BacklogFieldType.MultipleList)
  case object Versions        extends FieldType("versions", BacklogFieldType.MultipleList)
  case object IssueLinks      extends FieldType("issuelinks", BacklogFieldType.MultipleList)
  case object User            extends FieldType("user", BacklogFieldType.Text)
  case object Users           extends FieldType("users", BacklogFieldType.MultipleList)
  case object Status          extends FieldType("status", BacklogFieldType.Text)
  case object Components      extends FieldType("components", BacklogFieldType.MultipleList)
  case object TimeTracking    extends FieldType("timetracking", BacklogFieldType.Text)
  case object WorkLogs        extends FieldType("worklog", BacklogFieldType.MultipleList)
  case object Option          extends FieldType("option", BacklogFieldType.SingleList)
  case object OptionWithChild extends FieldType("option-with-child", BacklogFieldType.SingleList)
  case object SubTasks        extends FieldType("subtasks", BacklogFieldType.MultipleList)
  case object Progress        extends FieldType("progress", BacklogFieldType.Text)
  case object Unknown         extends FieldType("", BacklogFieldType.Text)

  // Customizable types
  case object Radio        extends FieldType("radio", BacklogFieldType.Radio)
  case object Checkbox     extends FieldType("checkbox", BacklogFieldType.CheckBox)
  case object CustomLabels extends FieldType("labels", BacklogFieldType.MultipleList)
  case object MultiSelect  extends FieldType("multiselect", BacklogFieldType.MultipleList)
  case object SingleSelect extends FieldType("select", BacklogFieldType.SingleList)
  case object TextArea     extends FieldType("textarea", BacklogFieldType.TextArea)

  def fromString(value: String): FieldType =
    value match {
      case String.value          => String
      case Number.value          => Number
      case DateTime.value        => DateTime
      case Date.value            => Date
      case IssueType.value       => IssueType
      case Project.value         => Project
      case FixVersions.value     => FixVersions
      case Resolution.value      => Resolution
      case Priority.value        => Priority
      case Labels.value          => Labels
      case Versions.value        => Versions
      case IssueLinks.value      => IssueLinks
      case User.value            => User
      case Status.value          => Status
      case Components.value      => Components
      case TimeTracking.value    => TimeTracking
      case SubTasks.value        => SubTasks
      case Progress.value        => Progress
      case WorkLogs.value        => WorkLogs
      case Option.value          => Option
      case OptionWithChild.value => OptionWithChild
      case _                     => Unknown
    }

  def apply(
      schemaType: Option[String],
      schemaSystem: Option[String],
      schemaItems: Option[String],
      schemaCustom: Option[String]
  ): FieldType =
    (schemaType, schemaSystem, schemaItems, schemaCustom) match {
      // Check array
      case (Some(FieldValueType.Multi.value), Some(User.value), _, _) => Users
      case (Some(FieldValueType.Multi.value), _, Some(Option.value), _) =>
        Checkbox
      case (
            Some(FieldValueType.Multi.value),
            _,
            Some(String.value),
            Some(custom)
          ) if custom.contains(CustomLabels.value) =>
        CustomLabels
      case (Some(FieldValueType.Multi.value), Some(name), _, _) =>
        fromString(name)
      case (Some(FieldValueType.Multi.value), None, _, _) => Strings
      // Check customizable
      case (Some(String.value), None, None, Some(custom)) if custom.contains(Radio.value) =>
        Radio
      case (Some(String.value), None, None, Some(custom)) if custom.contains(Checkbox.value) =>
        Checkbox
      case (Some(String.value), None, None, Some(custom)) if custom.contains(CustomLabels.value) =>
        CustomLabels
      case (Some(String.value), None, None, Some(custom)) if custom.contains(MultiSelect.value) =>
        MultiSelect
      case (Some(String.value), None, None, Some(custom)) if custom.contains(SingleSelect.value) =>
        SingleSelect
      case (Some(String.value), None, None, Some(custom)) if custom.contains(TextArea.value) =>
        TextArea
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
  case object Multi  extends FieldValueType("array")
}
