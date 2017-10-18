package com.nulabinc.backlog.j2b.jira.service

sealed trait IssueIOError
case object IssueWritingError extends IssueIOError
case class IssueReadingError(message: String) extends IssueIOError
case class IssueFetchingError(message: String) extends IssueIOError