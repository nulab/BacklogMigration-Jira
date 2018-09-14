package com.nulabinc.backlog.j2b.interpreters

import cats.~>
import com.nulabinc.backlog.j2b.dsl.ConsoleDSL.ConsoleProgram
import com.nulabinc.backlog.j2b.dsl.{ConsoleADT, Print}
import com.nulabinc.backlog.migration.common.utils.ConsoleOut
import monix.eval.Task

import scala.language.higherKinds

trait ConsoleInterpreter[F[_]] extends (ConsoleADT ~> F) {

  def run[A](program: ConsoleProgram[A]): F[A]

  def print(message: String): F[Unit]

  override def apply[A](fa: ConsoleADT[A]): F[A] = fa match {
    case Print(message) =>
      print(message)
  }

}

case class AsyncConsoleInterpreter() extends ConsoleInterpreter[Task] {

  override def run[A](program: ConsoleProgram[A]): Task[A] =
    program.foldMap(this)

  override def print(message: String): Task[Unit] = Task {
    ConsoleOut.println(message)
  }

}