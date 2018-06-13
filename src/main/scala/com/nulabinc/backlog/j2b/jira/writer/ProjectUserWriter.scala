package com.nulabinc.backlog.j2b.jira.writer

import com.nulabinc.backlog.migration.common.domain.BacklogUser

trait ProjectUserWriter {

  def write(users: Seq[BacklogUser]): Either[WriteError, Seq[BacklogUser]]

}
