package com.nulabinc.backlog.j2b.issue.writer

import com.nulabinc.backlog.j2b.jira.domain.export.Milestone
import com.nulabinc.backlog.migration.common.convert.BacklogUnmarshaller
import com.nulabinc.backlog.migration.common.utils.DateUtil
import com.nulabinc.jira.client.domain.Version
import org.specs2.mutable.Specification


class VersionFileWriterSpec extends Specification with FileWriterTestHelper {

  "should write versions to file" >> {

    val versions = Seq[Version](
      Version(id = None, name = "v1", description = Some("some version1"), archived = true, released = true, releaseDate = Some(DateUtil.yyyymmddParse("1988-04-16"))),
      Version(id = Some(124), name = "v2", description = Some("some version2"), archived = false, released = false, releaseDate = None)
    )

    // Output to file
    new VersionFileWriter().write(versions, Seq.empty[Milestone])

    val actual = BacklogUnmarshaller.versions(paths)

    actual.length               must beEqualTo(versions.length)
    actual(0).optId             must beNone
    actual(0).name              must beEqualTo(versions(0).name)
    actual(0).optReleaseDueDate must beSome("1988-04-16")
    actual(1).optId             must beEqualTo(versions(1).id)
    actual(1).name              must beEqualTo(versions(1).name)
    actual(1).optReleaseDueDate must beNone
  }
}
