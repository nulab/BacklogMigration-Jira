package com.nulabinc.backlog.j2b.jira.service

import com.nulabinc.backlog.j2b.jira.JiraError

sealed trait ExportError extends JiraError

case object VersionError extends ExportError
