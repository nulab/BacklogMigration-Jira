package com.nulabinc.backlog.j2b.conf

sealed trait ConfigValidateResult

case class Success() extends ConfigValidateResult

case class Failure(reason: String) extends ConfigValidateResult
