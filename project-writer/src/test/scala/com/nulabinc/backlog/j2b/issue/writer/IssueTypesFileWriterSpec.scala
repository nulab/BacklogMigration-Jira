package com.nulabinc.backlog.j2b.issue.writer

import com.nulabinc.backlog.migration.common.convert.BacklogUnmarshaller
import com.nulabinc.jira.client.domain.issue.IssueType
import org.specs2.mutable.Specification

class IssueTypesFileWriterSpec extends Specification with FileWriterTestHelper {

  "should write issue types to file" >> {

    val issueTypes = Seq[IssueType](
      IssueType(id = 888, name = "type1", isSubTask = true, description = "some issue type1"),
      IssueType(id = 889, name = "type2", isSubTask = false, description = "some issue type2")
    )

    // Output to file
    new IssueTypeFileWriter().write(issueTypes)

    val actual = BacklogUnmarshaller.issueTypes(paths)

    actual.length       must beEqualTo(issueTypes.length)
    actual(0).optId.get must beEqualTo(issueTypes(0).id)
    actual(0).name      must beEqualTo(issueTypes(0).name)
    actual(1).optId.get must beEqualTo(issueTypes(1).id)
    actual(1).name      must beEqualTo(issueTypes(1).name)
  }
}
