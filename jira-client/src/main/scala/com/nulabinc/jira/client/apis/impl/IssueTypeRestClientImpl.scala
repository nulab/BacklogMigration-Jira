package com.nulabinc.jira.client.apis.impl

import com.nulabinc.jira.client._
import com.nulabinc.jira.client.apis.IssueTypeRestClient
import com.nulabinc.jira.client.domain.issue.IssueType
import spray.json._

class IssueTypeRestClientImpl(httpClient: HttpClient) extends IssueTypeRestClient {

  import com.nulabinc.jira.client.json.IssueTypeMappingJsonProtocol._

  override def allIssueTypes() =
    httpClient.get("/issuetype") match {
      case Right(json)               => Right(JsonParser(json).convertTo[Seq[IssueType]])
      case Left(_: ApiNotFoundError) => Left(ResourceNotFoundError("IssueType", ""))
      case Left(error)               => Left(HttpError(error.toString))
    }
}
