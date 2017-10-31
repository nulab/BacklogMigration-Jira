package com.nulabinc.jira.client.json

import com.nulabinc.jira.client.apis.IssueResult
import spray.json.DefaultJsonProtocol

object IssueResultMappingJsonProtocol extends DefaultJsonProtocol {
  import IssueMappingJsonProtocol._

  implicit val IssueResultFieldMappingFormat = jsonFormat2(IssueResult)
}
