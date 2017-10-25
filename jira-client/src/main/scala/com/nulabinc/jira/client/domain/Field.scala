package com.nulabinc.jira.client.domain

case class Field(
  id: String,
  name: String,
  schema: Option[FieldSchema]
)

case class FieldSchema(customId: Option[Long], schemaType: FieldSchemaType)

sealed trait FieldSchemaType

object FieldSchemaType {

  import spray.json.deserializationError

  def convert(typeName: String) =
    typeName match {
      case "number"             => NumberSchema
      case "string"             => StringSchema
      case "date"               => DateSchema
      case "array"              => ArraySchema
      case "datetime"           => DatetimeSchema
      case "user"               => UserSchema
      case "any"                => AnySchema
      case "option"             => OptionSchema
      case "option-with-child"  => OptionWithChildSchema
      case "issuetype"          => IssueTypeSchema
      case "project"            => ProjectSchema
      case "resolution"         => ResolutionSchema
      case "watches"            => WatchesSchema
      case "priority"           => PrioritySchema
      case "status"             => StatusSchema
      case "timetracking"       => TimeTrackingSchema
      case "securitylevel"      => SecurityLevelSchema
      case "progress"           => ProjectSchema
      case "comments-page"      => CommentsPageSchema
      case "votes"              => VotesSchema
      case s => deserializationError("Cannot deserialize FieldSchemaType: invalid input. Raw input: " + s)
    }
}

case object NumberSchema extends FieldSchemaType
case object StringSchema extends FieldSchemaType
case object DateSchema extends FieldSchemaType
case object ArraySchema extends FieldSchemaType
case object DatetimeSchema extends FieldSchemaType
case object UserSchema extends FieldSchemaType
case object AnySchema extends FieldSchemaType
case object OptionSchema extends FieldSchemaType
case object OptionWithChildSchema extends FieldSchemaType

case object IssueTypeSchema extends FieldSchemaType
case object ProjectSchema extends FieldSchemaType
case object ResolutionSchema extends FieldSchemaType
case object WatchesSchema  extends FieldSchemaType
case object PrioritySchema extends FieldSchemaType
case object StatusSchema extends FieldSchemaType
case object TimeTrackingSchema extends FieldSchemaType
case object SecurityLevelSchema extends FieldSchemaType
case object ProgressSchema extends FieldSchemaType
case object CommentsPageSchema extends FieldSchemaType
case object VotesSchema extends FieldSchemaType