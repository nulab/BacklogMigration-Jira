package com.nulabinc.jira.client.domain

case class User(accountId: String, displayName: String, emailAddress: Option[String]) {

  def identifyKey: String = accountId

}
