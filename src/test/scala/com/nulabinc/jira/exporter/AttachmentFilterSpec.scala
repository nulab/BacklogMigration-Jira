package com.nulabinc.jira.exporter

import java.util.Date

import com.nulabinc.backlog.j2b.exporter.AttachmentFilter
import com.nulabinc.jira.client.domain._
import com.nulabinc.jira.client.domain.changeLog.ChangeLog
import com.nulabinc.jira.client.domain.issue._
import org.specs2.mutable.Specification

class AttachmentFilterSpec extends Specification {

  "AttachmentFilter.filteredIssue should return filtered an issue without file2.txt" >> {

    val issue = Issue(
      id = 1,
      key = "key",
      summary = "summary",
      description = None,
      parent = None,
      assignee = None,
      components = Seq.empty[Component],
      fixVersions = Seq.empty[Version],
      issueFields = Seq.empty[IssueField],
      dueDate = None,
      timeTrack = None,
      issueType = IssueType(
        id = 1,
        name = "issue type",
        isSubTask = false,
        description = "description"
      ),
      status = Status("1", "status"),
      priority = Priority("priority"),
      creator = User(Some("key"), "name", "display", Some("mail")),
      createdAt =  new Date(),
      updatedAt =  new Date(),
      changeLogs = Seq.empty[ChangeLog],
      attachments = Seq(
        Attachment(
          id = 1,
          fileName = "file1.txt",
          author = User(Some("key1"), "user1", "user1", Some("mail1")),
          createdAt = new Date(),
          size = 100,
          mimeType = "mine",
          content = "data"
        ),
        Attachment(
          id = 2,
          fileName = "file2.txt",
          author = User(Some("key2"), "user2", "user2", Some("mail2")),
          createdAt = new Date(),
          size = 200,
          mimeType = "mine",
          content = "data"
        ),
        Attachment(
          id = 3,
          fileName = "file3.txt",
          author = User(Some("key3") ,"user3", "user3", Some("mail3")),
          createdAt = new Date(),
          size = 300,
          mimeType = "mine",
          content = "data"
        )
      )
    )

    val comments = Seq(
      Comment(
        id = 1,
        body = "test1 body [^file2.txt] ",
        author = User(Some("aaa"), "aaa", "aaa", Some("mmm")),
        createdAt = new Date()
      )
    )

    val actual = AttachmentFilter.filteredIssue(issue, comments)

    actual.attachments(0).fileName must beEqualTo("file1.txt")
  }
}