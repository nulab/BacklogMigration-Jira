package com.nulabinc.jira.client.domain

case class User(key: Option[String], name: String, displayName: String, emailAddress: String) {

  def identifyKey: String = key.getOrElse(name)

}
