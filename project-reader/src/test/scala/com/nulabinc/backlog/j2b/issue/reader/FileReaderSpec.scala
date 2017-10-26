package com.nulabinc.backlog.j2b.issue.reader

import java.io.PrintWriter

import com.nulabinc.jira.client.domain.{Issue, IssueField}
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

//class FileReaderSpec extends Specification with Mockito {
//
//  "should get issues from file" >> {
//
//    val filePath = "issue-reader/target/aaa.txt"
//
//    val pw = new PrintWriter(filePath)
//    pw.write("{\"id\":\"1\",\"key\":\"TEST-1\",\"fields\":{}}\n")
//    pw.close()
//
//    val reader = new FileReader
//    val expect = Seq(
//      Issue(1, "TEST-1", IssueField(None))
//    )
//    val issues = reader.read(filePath)
//
//    expect must beEqualTo(issues.right.get)
//  }
//}
