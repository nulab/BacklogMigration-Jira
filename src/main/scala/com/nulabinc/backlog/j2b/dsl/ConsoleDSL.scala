package com.nulabinc.backlog.j2b.dsl

import cats.free.Free

object ConsoleDSL {

  type ConsoleProgram[A] = Free[ConsoleADT, A]

  def print(message: String): ConsoleProgram[Unit] =
    Free.liftF(Print(message))

  def warn(message: String): ConsoleProgram[Unit] =
    Free.liftF(Warn(message))

  def error(message: String): ConsoleProgram[Unit] =
    Free.liftF(Error(message))

}