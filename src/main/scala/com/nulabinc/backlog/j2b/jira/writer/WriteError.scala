package com.nulabinc.backlog.j2b.jira.writer

import com.nulabinc.backlog.j2b.jira.JiraError

sealed trait WriteError extends JiraError

case object VersionWriteError extends WriteError
