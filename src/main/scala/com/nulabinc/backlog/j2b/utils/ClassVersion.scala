package com.nulabinc.backlog.j2b.utils

object ClassVersion {

  private[this] val CLASS_VERSION_8: Double = 52.0

  def isValid(): Boolean =
    System.getProperty("java.class.version").toDouble >= CLASS_VERSION_8
}
