package com.nulabinc.backlog.j2b.dsl

trait ConsoleADT[A]
case class Print(message: String) extends ConsoleADT[Unit]
