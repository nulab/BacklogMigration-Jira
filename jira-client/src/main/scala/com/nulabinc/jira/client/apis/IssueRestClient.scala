package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client.JiraRestClientError
import com.nulabinc.jira.client.domain.{Issue, IssueField}
import spray.json._

object IssueMappingJsonProtocol extends DefaultJsonProtocol {
  implicit val IssueFieldMappingFormat  = jsonFormat1(IssueField)
  implicit object IssueMappingFormat extends RootJsonFormat[Issue] {
    def write(item: Issue) = JsObject(
      "id"     -> JsString(item.id.toString),
      "key"    -> JsString(item.key),
      "fields" -> item.fields.toJson
    )
    def read(value: JsValue) = {
      val jsObject = value.asJsObject

      jsObject.getFields("id", "key", "fields") match {
        case Seq(JsString(id), JsString(key), fields) ⇒ Issue(
          id.toLong,
          key,
          IssueFieldMappingFormat.read(fields)
        )
        case other ⇒ deserializationError("Cannot deserialize Issue: invalid input. Raw input: " + other)
      }
    }
  }
}

trait IssueRestClient {

  def issue(id: Long): Either[JiraRestClientError, Issue]

  def issue(key: String): Either[JiraRestClientError, Issue]

  def projectIssues(key: String): Either[JiraRestClientError, Seq[Issue]]
}
