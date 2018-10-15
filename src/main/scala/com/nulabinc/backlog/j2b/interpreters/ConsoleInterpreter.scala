package com.nulabinc.backlog.j2b.interpreters

import cats.~>
import com.nulabinc.backlog.j2b.dsl.ConsoleDSL.ConsoleProgram
import com.nulabinc.backlog.j2b.dsl._
import com.nulabinc.backlog.migration.common.utils.ConsoleOut
import monix.eval.Task
import org.fusesource.jansi.AnsiConsole

import scala.language.higherKinds

trait ConsoleInterpreter[F[_]] extends (ConsoleADT ~> F) {

  def run[A](program: ConsoleProgram[A]): F[A]

  def print(message: String): F[Unit]

  def warn(message: String): F[Unit]

  def error(message: String): F[Unit]

  def terminate(): F[Unit]

  def apply[A](fa: ConsoleADT[A]): F[A] = fa match {
    case Print(message) =>
      print(message)
    case Warn(message) =>
      warn(message)
    case Error(message) =>
      error(message)
  }

}

case class AsyncConsoleInterpreter() extends ConsoleInterpreter[Task] {

  def run[A](program: ConsoleProgram[A]): Task[A] =
    program.foldMap(this)

  def print(message: String): Task[Unit] = Task {
    ConsoleOut.println(message)
  }

  def warn(message: String): Task[Unit] = Task {
    ConsoleOut.warning(message)
  }

  def error(message: String): Task[Unit] = Task {
    ConsoleOut.error(message)
  }

  def terminate(): Task[Unit] = Task {
    AnsiConsole.systemUninstall()
  }

}