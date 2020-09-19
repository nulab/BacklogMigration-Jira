package com.nulabinc.backlog.j2b.mapping.converter.writes

import com.nulabinc.backlog.migration.common.convert.Writes
import com.nulabinc.backlog.migration.common.domain._

private[converter] class CommentWrites extends Writes[BacklogComment, BacklogComment] {

  override def writes(comment: BacklogComment): BacklogComment = comment

}
