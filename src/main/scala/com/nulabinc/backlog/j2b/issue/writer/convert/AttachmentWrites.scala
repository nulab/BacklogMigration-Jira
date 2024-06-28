package com.nulabinc.backlog.j2b.issue.writer.convert

import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain.BacklogAttachment
import com.nulabinc.backlog.migration.common.utils.FileUtil
import com.nulabinc.jira.client.domain.Attachment

class AttachmentWrites extends Writes[Attachment, BacklogAttachment] {

  override def writes(attachment: Attachment) =
    BacklogAttachment(
      optId = Some(attachment.id),
      name = FileUtil.clean(attachment.fileName)
    )
}
