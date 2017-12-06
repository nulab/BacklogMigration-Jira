package com.nulabinc.jira.client.domain.field

sealed trait FieldCustomType
case object FirstResponseDate extends FieldCustomType

case object MultiSelect extends FieldCustomType
case object Select extends FieldCustomType
case object Textarea extends FieldCustomType
case object Textfield extends FieldCustomType
case object RadioButtons extends FieldCustomType
case object MultiCheckBoxes extends FieldCustomType
case object CustomLabel extends FieldCustomType

object FieldCustomType {

  def convert(typeName: String) =
    typeName match {
      case "com.atlassian.jira.plugin.system.customfieldtypes:multiselect"      => Some(MultiSelect)  // multi select
      case "com.atlassian.jira.plugin.system.customfieldtypes:select"           => Some(Select)       // single select
      case "com.atlassian.jira.plugin.system.customfieldtypes:textarea"         => Some(Textarea)     // multi line
      case "com.atlassian.jira.plugin.system.customfieldtypes:textfield"        => Some(Textfield)    // single line
      case "com.atlassian.jira.plugin.system.customfieldtypes:radiobuttons"     => Some(RadioButtons)
      case "com.atlassian.jira.plugin.system.customfieldtypes:multicheckboxes"  => Some(MultiCheckBoxes)
      case "com.atlassian.jira.plugin.system.customfieldtypes:labels"           => Some(CustomLabel)      // multi select
      case _ => None
    }
}
