package com.nulabinc.jira.client.apis

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.domain.field.Field
import spray.json._

class FieldAPI(httpClient: HttpClient) {

  import com.nulabinc.jira.client.json.FieldMappingJsonProtocol._

  def all() =
    httpClient.get(s"/field") match {
      case Right(json)               => Right(JsonParser(json).convertTo[Seq[Field]])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("Field", "all"))
      case Left(error)               => Left(HttpError(error.toString))
    }

}
