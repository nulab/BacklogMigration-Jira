package com.nulabinc.backlog.j2b.persistence.store.sqlite.tables

import com.nulabinc.backlog.j2b.persistence.store.sqlite.{JiraStatusMapping, JiraStatusMappingItem}
import com.nulabinc.backlog.migration.common.persistence.sqlite.tables.StatusMappingTable
import slick.ast.BaseTypedType
import slick.jdbc.JdbcType
import slick.jdbc.SQLiteProfile.api._
import slick.lifted.{ProvenShape, Tag}

class JiraStatusMappingTable(tag: Tag) extends StatusMappingTable[JiraStatusMapping, JiraStatusMappingItem](tag) {

  implicit val jiraStatusMappingItemMapper: JdbcType[JiraStatusMappingItem] with BaseTypedType[JiraStatusMappingItem] =
    MappedColumnType.base[JiraStatusMappingItem, String](
      src => src.value,
      dst => JiraStatusMappingItem(dst)
    )

  def optSrc: Rep[Option[JiraStatusMappingItem]] = column[Option[JiraStatusMappingItem]]("src")

  override def * : ProvenShape[JiraStatusMapping] =
    (id, optSrc, optDst) <> (JiraStatusMapping.tupled, JiraStatusMapping.unapply)
}
