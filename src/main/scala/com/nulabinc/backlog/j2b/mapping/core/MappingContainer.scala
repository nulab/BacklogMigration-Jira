package com.nulabinc.backlog.j2b.mapping.core

import com.nulabinc.backlog.j2b.jira.domain.mapping.Mapping

case class MappingContainer(user: Seq[Mapping], priority: Seq[Mapping], status: Seq[Mapping])
