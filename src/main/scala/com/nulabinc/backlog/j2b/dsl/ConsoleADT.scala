package com.nulabinc.backlog.j2b.dsl

sealed trait ConsoleADT[A]
case class Print(message: String) extends ConsoleADT[Unit]
