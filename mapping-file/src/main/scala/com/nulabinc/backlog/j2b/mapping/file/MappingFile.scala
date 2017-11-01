package com.nulabinc.backlog.j2b.mapping.file

import com.nulabinc.backlog.j2b.jira.domain.{Mapping, MappingsWrapper}
import com.nulabinc.backlog.j2b.jira.domain.MappingJsonProtocol._
import com.nulabinc.backlog.migration.common.utils.{IOUtil, Logging}
import spray.json.{JsonParser, _}

import scala.collection.mutable.ArrayBuffer
import scala.util.Try
import scalax.file.Path
import scalax.io.{Codec, Resource}

trait MappingFile extends Logging {

  def matchItem(jira: MappingItem): String

  def backlogs: Seq[MappingItem]

  def jiras: Seq[MappingItem]

  def filePath: String

  def itemName: String

  def description: String

  def isDisplayDetail: Boolean

  def isValid: Boolean = errors.isEmpty

  def isExists: Boolean = Path.fromString(filePath).exists

  def isParsed: Boolean = unmarshal().isDefined

  def create() = {
    val wrapper = MappingsWrapper(description, jiras.map(convert))
    IOUtil.output(Path.fromString(filePath), wrapper.toJson.prettyPrint)
  }

  def merge(): Seq[Mapping] = {
    unmarshal() match {
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
        IOUtil.output(Path.fromString(filePath), MappingsWrapper(description, mergeList).toJson.prettyPrint)
        addedList
      case _ =>
        Seq.empty[Mapping]
    }
  }

  def unmarshal(): Option[Seq[Mapping]] = {
    val path: Path = Path.fromString(filePath)
    val json       = path.lines().mkString
    try {
      val wrapper: MappingsWrapper = JsonParser(json).convertTo[MappingsWrapper]
      Some(wrapper.mappings)
    } catch {
      case e: Throwable =>
        logger.error(e.getMessage, e)
        None
    }
  }

//  def tryUnmarshal(): Try[Seq[Mapping]] = {
//    Try(Resource.fromFile(filePath))
//      .map(input => input.string(Codec.UTF8))
//      .map(json => JsonParser(json).convertTo[MappingsWrapper].mappings)
//  }

  def tryUnmarshal(): Seq[Mapping] = {
    val path = Path.fromString(filePath)
    val json = path.lines().mkString
    JsonParser(json).convertTo[MappingsWrapper].mappings
  }

  def errors: Seq[String] = {
    val fileName  = Path.fromString(filePath).name
    val validator = new MappingValidator(jiras, backlogs, itemName, fileName)
    validator.validate(unmarshal())
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
      mappingType = "",
      src = jira.name,
      dst = matchItem(jira)
    )

}
