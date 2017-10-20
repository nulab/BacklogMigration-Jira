package com.nulabinc.backlog.j2b.mapping.collector.core

import com.nulabinc.jira.client.domain.{Status, User}

case class MappingData(users: Set[User], statuses: Set[Status])
