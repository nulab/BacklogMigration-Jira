package com.nulabinc.backlog.j2b.jira.domain.mapping

import java.util.Locale
import better.files.File
import com.nulabinc.backlog.j2b.jira.domain.mapping.MappingJsonProtocol._
import com.nulabinc.backlog.migration.common.utils.{IOUtil, Logging}
import com.osinka.i18n.{Lang, Messages}
import spray.json._

import scala.collection.mutable.ArrayBuffer

trait MappingFile extends Logging {

  def matchItem(jira: MappingItem): String

  def backlogs: Seq[MappingItem]

  def jiras: Seq[MappingItem]

  def filePath: String

  def itemName: String

  def description: String

  def isDisplayDetail: Boolean

  def isValid: Boolean = errors.isEmpty

  def isExists: Boolean = {
    val path: File = File(filePath).path.toAbsolutePath
    !path.isDirectory && path.exists
  }

  def isParsed: Boolean = unMarshal().isDefined

  def create(): MappingFile = {
    val wrapper = MappingsWrapper(description, jiras.map(convert))
    IOUtil.output(File(filePath).path.toAbsolutePath, wrapper.toJson.prettyPrint)
    this
  }

  def merge(): Seq[Mapping] = {
    unMarshal() match {
      case Some(currentItems) =>
        val mergeList: ArrayBuffer[Mapping] = ArrayBuffer()
        val addedList: ArrayBuffer[Mapping] = ArrayBuffer()
        jiras.foreach { jiraItem =>
          val optCurrentItem = currentItems.find(_.src == jiraItem.name)
          optCurrentItem match {
            case Some(currentItem) => mergeList += currentItem
            case _ =>
              mergeList += convert(jiraItem)
              addedList += convert(jiraItem)
          }
        }
        IOUtil.output(File(filePath).path.toAbsolutePath, MappingsWrapper(description, mergeList).toJson.prettyPrint)
        addedList
      case _ =>
        Seq.empty[Mapping]
    }
  }

  def unMarshal(): Option[Seq[Mapping]] = {
    val path = File(filePath).path.toAbsolutePath
    val json = IOUtil.input(path).getOrElse("")
    try {
      val wrapper: MappingsWrapper = JsonParser(json).convertTo[MappingsWrapper]
      Some(wrapper.mappings)
    } catch {
      case e: Throwable =>
        logger.error(e.getMessage, e)
        None
    }
  }

  def tryUnMarshal(): Seq[Mapping] = {
    val path = File(filePath).path.toAbsolutePath
    val json = IOUtil.input(path).getOrElse("")
    JsonParser(json).convertTo[MappingsWrapper].mappings
  }

//  def tryUnmarshal(): Try[Seq[Mapping]] = {
//    Try(Resource.fromFile(filePath))
//      .map(input => input.string(Codec.UTF8))
//      .map(json => JsonParser(json).convertTo[MappingsWrapper].mappings)
//  }

  def errors: Seq[String] = {
    val validator = new MappingValidator(jiras, backlogs, itemName)
    validator.validate(unMarshal())
  }

  def display(name: String, mappingItems: Seq[MappingItem]): String =
    mappingItems.find(_.name == name) match {
      case Some(mappingItem) =>
        if (isDisplayDetail) s"${mappingItem.display}(${mappingItem.name})"
        else name
      case _ => name
    }

  private[this] def convert(jira: MappingItem): Mapping =
    Mapping(
      info = None,
      src = jira.name,
      dst = matchItem(jira)
    )



  private class MappingValidator(jiraMappings: Seq[MappingItem],
                                 backlogMappings: Seq[MappingItem],
                                 itemName: String) {

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

    private def itemsExists(mappings: Seq[Mapping], checkService: String): Seq[String] = {
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

    private def itemExists(value: String, mappingItems: Seq[MappingItem], serviceName: String): Option[String] = {
      if (value.nonEmpty && !mappingItems.exists(_.name == value)) {
        Some(s"- ${Messages("cli.mapping.error.not_exist.item", itemName, value, serviceName)}")
      } else None
    }

    private  def itemsRequired(mappings: Seq[Mapping], checkService: String): Seq[String] = {
      mappings.foldLeft(Seq.empty[String])((errors: Seq[String], mapping: Mapping) => {
        itemRequired(mapping, checkService) match {
          case Some(error) => errors :+ error
          case None        => errors
        }
      })
    }

    private def itemRequired(mapping: Mapping, checkService: String): Option[String] = {
      if (checkService == CHECK_JIRA) {
        if (mapping.src.isEmpty) Some(s"- ${Messages("cli.mapping.error.empty.item", Messages("common.backlog"), itemName, mapping.dst)}")
        else None
      } else {
        if (mapping.dst.isEmpty) Some(s"- ${Messages("cli.mapping.error.empty.item", Messages("common.jira"), itemName, mapping.src)}")
        else None
      }
    }
  }
}
