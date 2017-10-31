package com.nulabinc.jira.client.domain

import com.nulabinc.jira.client.domain.issue.Issue

case class SearchResult(startAt: Int,
                        maxResults: Int,
                        total: Int,
                        issues: Seq[Issue]
                       )
