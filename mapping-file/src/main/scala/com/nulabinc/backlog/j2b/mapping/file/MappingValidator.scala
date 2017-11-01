package com.nulabinc.backlog.j2b.mapping.file

import java.util.Locale

import com.nulabinc.backlog.j2b.jira.domain.Mapping
import com.osinka.i18n.{Lang, Messages}

private [this] class MappingValidator(jiraMappings: Seq[MappingItem],
                                      backlogMappings: Seq[MappingItem],
                                      itemName: String,
                                      fileName: String) {

  implicit val userLang = if (Locale.getDefault.equals(Locale.JAPAN)) Lang("ja") else Lang("en")

  val CHECK_JIRA    = "CHECK_JIRA"
  val CHECK_BACKLOG = "CHECK_BACKLOG"

  def validate(optMappings: Option[Seq[Mapping]]): Seq[String] = {
    optMappings match {
      case Some(mappings) =>
        itemsExists(mappings, CHECK_JIRA) union
        itemsRequired(mappings, CHECK_JIRA) union
        itemsExists(mappings, CHECK_BACKLOG) union
        itemsRequired(mappings, CHECK_BACKLOG)
      case _ => throw new RuntimeException
    }
  }

  private[this] def itemsExists(mappings: Seq[Mapping], checkService: String): Seq[String] = {
    mappings.foldLeft(Seq.empty[String])((errors: Seq[String], mapping: Mapping) =>
      if (checkService == CHECK_JIRA) {
        itemExists(mapping.src, jiraMappings, Messages("common.jira")) match {
          case Some(error) => errors :+ error
          case None        => errors
        }
      } else {
        itemExists(mapping.dst, backlogMappings, Messages("common.backlog")) match {
          case Some(error) => errors :+ error
          case None        => errors
        }
      })
  }

  private[this] def itemExists(value: String, mappingItems: Seq[MappingItem], serviceName: String): Option[String] = {
    if (value.nonEmpty && !mappingItems.exists(_.name == value)) {
      Some(s"- ${Messages("cli.mapping.error.not_exist.item", itemName, value, serviceName)}")
    } else None
  }

  private[this] def itemsRequired(mappings: Seq[Mapping], checkService: String): Seq[String] = {
    mappings.foldLeft(Seq.empty[String])((errors: Seq[String], mapping: Mapping) => {
      itemRequired(mapping, checkService) match {
        case Some(error) => errors :+ error
        case None        => errors
      }
    })
  }

  private[this] def itemRequired(mapping: Mapping, checkService: String): Option[String] = {
    if (checkService == CHECK_JIRA) {
      if (mapping.src.isEmpty) Some(s"- ${Messages("cli.mapping.error.empty.item", Messages("common.backlog"), itemName, mapping.dst)}")
      else None
    } else {
      if (mapping.dst.isEmpty) Some(s"- ${Messages("cli.mapping.error.empty.item", Messages("common.jira"), itemName, mapping.src)}")
      else None
    }
  }
}
