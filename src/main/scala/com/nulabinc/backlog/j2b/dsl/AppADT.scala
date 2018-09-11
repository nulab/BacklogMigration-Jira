package com.nulabinc.backlog.j2b.dsl

import com.nulabinc.backlog.j2b.dsl.ConsoleDSL.ConsoleProgram

sealed trait AppADT[A]

case class Pure[A](a: A) extends AppADT[A]
case class FromConsole[A](program: ConsoleProgram[A]) extends AppADT[A]
