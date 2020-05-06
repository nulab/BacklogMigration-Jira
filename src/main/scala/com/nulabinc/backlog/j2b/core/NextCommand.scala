package com.nulabinc.backlog.j2b.core

import com.nulabinc.backlog.j2b.Config
import com.nulabinc.backlog.migration.common.conf.BacklogConfiguration

object NextCommand extends BacklogConfiguration {

  def command(args: Seq[String]): String = {
    val lists = Seq(
      "java -jar",
      s"  ${Config.Application.fileName}",
      "  import"
    ) ++ formattedArgs(args)
    lists.mkString(" \\ \n")
  }

  private def formattedArgs(args: Seq[String]): Seq[String] =
    args
      .filterNot(_ == "export")
      .grouped(2)
      .collect {
        case Seq(k, _) if k.contains("apiKey") =>
          language match {
            case "ja" => s"    $k JIRAのAPIキー"
            case "en" => s"    $k JIRA_API_KEY"
            case _    => s"    $k JIRA_API_KEY"
          }
        case Seq(k, v) => s"    $k $v"
      }
      .toSeq
}
