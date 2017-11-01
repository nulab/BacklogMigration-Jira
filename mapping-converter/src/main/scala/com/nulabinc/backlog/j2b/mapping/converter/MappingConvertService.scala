package com.nulabinc.backlog.j2b.mapping.converter

import javax.inject.Inject

import com.nulabinc.backlog.j2b.jira.converter.MappingConverter
import com.nulabinc.backlog.j2b.mapping.converter.writes.IssueWrites
import com.nulabinc.backlog.migration.common.conf.BacklogPaths
import com.nulabinc.backlog.migration.common.convert.{BacklogUnmarshaller, Convert}
import com.nulabinc.backlog.migration.common.domain.{BacklogComment, BacklogIssue}
import com.nulabinc.backlog.migration.common.utils.IOUtil

import scala.collection.mutable
import scalax.file.Path

class MappingConvertService @Inject()(implicit val issueWrites: IssueWrites,
                                      backlogPaths: BacklogPaths) extends MappingConverter {

  def convert(): Unit = {

    issues()


  }

  private def issues() = {
    val paths: Seq[Path] = IOUtil.directoryPaths(backlogPaths.issueDirectoryPath)
      .flatMap(_.toAbsolute.children().filter(_.isDirectory).toSeq)
    paths.zipWithIndex.foreach {
      case (path, index) => println(path + " - " + index)
        // convertIssue(path, index, paths.size)
    }
  }

//  private def convertIssue(path: Path, index: Int, size: Int) = {
//    BacklogUnmarshaller.issue(backlogPaths.issueJson(path)) match {
//      case Some(issue: BacklogIssue) =>
//        IOUtil.output(backlogPaths.issueJson(path), Convert.toBacklog(issue).toJson.prettyPrint)
//      case Some(comment: BacklogComment) =>
//        IOUtil.output(backlogPaths.issueJson(path), Convert.toBacklog(comment).toJson.prettyPrint)
//      case _ => throw new RuntimeException(s"Issue file not found.:${backlogPaths.issueJson(path).path}")
//    }
////    issueConsoleProgress(index + 1, size)
//  }

}
