package com.nulabinc.backlog.j2b.mapping.core

import java.io.File

object MappingDirectory {

  private[this] val WORKING_DIRECTORY = new File(".").getAbsoluteFile.getParent
  val ROOT = WORKING_DIRECTORY + "/mapping"
  val USER_MAPPING_FILE = ROOT + "/users.csv"
  val STATUS_MAPPING_FILE = ROOT + "/statuses.csv"
  val PRIORITY_MAPPING_FILE = ROOT + "/priorities.csv"
  val USER_MAPPING_LIST_FILE = ROOT + "/users_list.csv"
  val STATUS_MAPPING_LIST_FILE = ROOT + "/statuses_list.csv"
  val PRIORITY_MAPPING_LIST_FILE = ROOT + "/priorities_list.csv"
}
