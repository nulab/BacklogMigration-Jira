package com.nulabinc.backlog.j2b.issue.writer

import com.nulabinc.backlog.j2b.jira.domain.JiraProjectKey
import com.nulabinc.jira.client.domain.{Issue, IssueField}
import com.nulabinc.jira.client.JiraRestClient
import org.mockito.Answers._
import org.mockito.Mockito.withSettings
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

import scalax.file.Path

class FileWriterSpec extends Specification with Mockito {

  "should write issues to file" >> {

    val filePath = "issue-writer/target/aaa.txt"
    val jira: JiraRestClient = mock[JiraRestClient](withSettings.defaultAnswer(RETURNS_DEEP_STUBS.get))
    val issues = Seq(
      Issue(1, "TEST-1", IssueField(None))
    )
    jira.issueRestClient.projectIssues("TEST", 0, 100) returns Right(issues)
    jira.issueRestClient.projectIssues("TEST", 100, 100) returns Right(Seq.empty[Issue])

    val writer = new FileWriter(jira)
    val actual = writer.write(JiraProjectKey("TEST"), filePath)

    // Check write result
    actual.right.get.length must beEqualTo(1)

    // Check the file exists
    Path.fromString(filePath).isFile must beTrue
  }

}
