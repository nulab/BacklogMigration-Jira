package com.nulabinc.jira.client.domain

case class SearchResult(startAt: Int,
                        maxResults: Int,
                        total: Int,
                        issues: Seq[Issue]
                       )
