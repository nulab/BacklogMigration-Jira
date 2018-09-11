package com.nulabinc.backlog.j2b.dsl

import cats.free.Free

object ConsoleDSL {

  type ConsoleProgram[A] = Free[ConsoleADT, A]

  def print(message: String): ConsoleProgram[Unit] =
    Free.liftF(Print(message))

}
