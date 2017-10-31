package com.nulabinc.jira.client.domain.issue

case class TimeTrack(
  originalEstimateSeconds: Option[Int],
  timeSpentSeconds: Option[Int]
)
