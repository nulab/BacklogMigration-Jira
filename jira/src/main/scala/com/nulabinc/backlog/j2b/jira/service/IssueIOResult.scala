package com.nulabinc.backlog.j2b.jira.service

sealed trait IssueIOResult
case object IssueIODone extends IssueIOResult
case object IssueIOFails extends IssueIOResult