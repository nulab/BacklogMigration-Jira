package com.nulabinc.jira.client.domain.field

sealed trait FieldCustomType
case object FirstResponseDate extends FieldCustomType
case object Labels extends FieldCustomType

object FieldCustomType {

  def convert(typeName: String) =
    typeName match {
      case "com.atlassian.jira.ext.charting:firstresponsedate"        => Some(FirstResponseDate)
      case "com.atlassian.jira.plugin.system.customfieldtypes:labels" => Some(Labels)
      case _ => None
    }
}
