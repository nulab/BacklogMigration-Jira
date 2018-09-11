package com.nulabinc.backlog.j2b.interpreters

import cats.~>
import com.nulabinc.backlog.j2b.dsl.ConsoleDSL.ConsoleProgram
import com.nulabinc.backlog.j2b.dsl.{ConsoleADT, Print}
import com.nulabinc.backlog.migration.common.utils.ConsoleOut

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

trait ConsoleInterpreter[F[_]] extends (ConsoleADT ~> F) {

  def run[A](program: ConsoleProgram[A]): F[A]

  def print(message: String): F[Unit]

  override def apply[A](fa: ConsoleADT[A]): F[A] = fa match {
    case Print(message) =>
      print(message)
  }

}

case class AsyncConsoleInterpreter()(implicit exc: ExecutionContext) extends ConsoleInterpreter[Future] {

  import cats.implicits._

  override def run[A](program: ConsoleProgram[A]): Future[A] =
    program.foldMap(this)

  override def print(message: String): Future[Unit] = Future.successful {
    ConsoleOut.println(message)
  }

}