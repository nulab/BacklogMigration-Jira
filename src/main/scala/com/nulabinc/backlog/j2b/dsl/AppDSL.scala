package com.nulabinc.backlog.j2b.dsl

import cats.free.Free
import com.nulabinc.backlog.j2b.dsl.ConsoleDSL.ConsoleProgram

object AppDSL {

  type AppProgram[A] = Free[AppADT, A]

  def pure[A](a: A): AppProgram[A] =
    Free.liftF(Pure(a))

  def fromConsole[A](program: ConsoleProgram[A]): AppProgram[A] =
    Free.liftF(FromConsole(program))

  def setLanguage(lang: String): AppProgram[Unit] =
    Free.liftF(SetLanguage(lang))

  def exit(statusCode: Int): AppProgram[Unit] =
    Free.liftF(Exit(statusCode))

}
