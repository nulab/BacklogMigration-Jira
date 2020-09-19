package com.nulabinc.backlog.j2b.jira.domain

import com.nulabinc.backlog.migration.common.domain.support.{Identifier, Undefined}

class JiraProjectKey(projectKey: String) extends Identifier[String] {

  def value = projectKey

}

object JiraProjectKey {
  val undefined = new JiraProjectKey("") with Undefined

  def apply(value: String): JiraProjectKey = new JiraProjectKey(value)
}
