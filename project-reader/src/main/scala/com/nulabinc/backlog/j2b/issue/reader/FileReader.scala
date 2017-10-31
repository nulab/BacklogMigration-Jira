package com.nulabinc.backlog.j2b.issue.reader

import com.nulabinc.backlog.j2b.jira.service.{IssueIOError, IssueReadService, IssueReadingError}
import com.nulabinc.backlog.migration.common.utils.Logging
import com.nulabinc.jira.client.domain.issue.Issue
import spray.json._

import scalax.file.Path

class FileReader extends IssueReadService with Logging {

  import com.nulabinc.jira.client.json.IssueMappingJsonProtocol._

  override def read(filePath: String): Either[IssueIOError, Seq[Issue]] = {
    try {
      val issues = Path.fromString(filePath).lines().map { line =>
        JsonParser(line).convertTo[Issue]
      }
      Right(issues.toSeq)
    } catch {
      case e: Throwable =>
        logger.error(e.getMessage, e)
        Left(IssueReadingError(e.getMessage))
    }
  }

}
