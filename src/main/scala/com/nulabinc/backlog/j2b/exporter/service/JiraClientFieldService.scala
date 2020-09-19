package com.nulabinc.backlog.j2b.exporter.service

import com.nulabinc.backlog.j2b.jira.service.FieldService
import com.nulabinc.jira.client.JiraRestClient
import javax.inject.Inject

class JiraClientFieldService @Inject() (jira: JiraRestClient) extends FieldService {

  override def all() =
    jira.fieldAPI.all.right.get

}
