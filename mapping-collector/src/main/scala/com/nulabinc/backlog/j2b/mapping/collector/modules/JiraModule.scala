package com.nulabinc.backlog.j2b.mapping.collector.modules

import com.nulabinc.backlog.j2b.jira.conf.JiraApiConfiguration
import com.nulabinc.backlog.j2b.modules.DefaultModule

private [collector] class JiraModule(apiConfig: JiraApiConfiguration)
  extends DefaultModule(apiConfig) {}

