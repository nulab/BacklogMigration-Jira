package com.nulabinc.backlog.j2b.issue.reader

import com.nulabinc.backlog.j2b.jira.service.IssueReadService
import com.nulabinc.backlog.migration.common.utils.Logging

class FileReader extends IssueReadService with Logging {

//  import com.nulabinc.jira.client.json.IssueMappingJsonProtocol._


//  override def read(filePath: String): Either[WriteError, Seq[Issue]] = {
//    try {
//      val issues = Path.fromString(filePath).lines().map { line =>
//        JsonParser(line).convertTo[Issue]
//      }
//      Right(issues.toSeq)
//    } catch {
//      case e: Throwable =>
//        logger.error(e.getMessage, e)
//        Left(IssueReadingError(e.getMessage))
//    }
//  }
  override def read(filePath: String) = ???
}
