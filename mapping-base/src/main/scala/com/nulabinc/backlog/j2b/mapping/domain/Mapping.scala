package com.nulabinc.backlog.j2b.mapping.domain

import spray.json.DefaultJsonProtocol

case class MappingsWrapper(description: String, mappings: Seq[Mapping])

case class Mapping(jira: String, backlog: String)

object MappingJsonProtocol extends DefaultJsonProtocol {
  implicit val MappingFormat         = jsonFormat2(Mapping)
  implicit val MappingsWrapperFormat = jsonFormat2(MappingsWrapper)
}
