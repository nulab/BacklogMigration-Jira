package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client.JiraRestClientError
import com.nulabinc.jira.client.domain.Project
import spray.json._

object ProjectMappingJsonProtocol extends DefaultJsonProtocol {
  implicit object IdFormat extends RootJsonFormat[Project] {
    def write(c: Project) = JsObject(
      "id"          -> JsString(c.id.toString),
      "key"         -> JsString(c.key),
      "description" -> JsString(c.description)
    )
    def read(value: JsValue) = {
      value.asJsObject.getFields("id", "key", "description") match {
        case Seq(JsString(id), JsString(key), JsString(description)) => Project(id.toLong, key, description)
        case _ => throw new DeserializationException("Project expected")
      }
    }
  }
}

trait ProjectRestClient {

  def project(id: Long): Either[JiraRestClientError, Project]

  def project(key: String): Either[JiraRestClientError, Project]
}
