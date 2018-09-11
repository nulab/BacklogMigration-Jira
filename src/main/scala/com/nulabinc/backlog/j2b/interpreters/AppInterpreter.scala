package com.nulabinc.backlog.j2b.interpreters

import cats.~>
import com.nulabinc.backlog.j2b.dsl.AppDSL.AppProgram
import com.nulabinc.backlog.j2b.dsl.{AppADT, FromConsole, Pure}
import com.nulabinc.backlog.j2b.dsl.ConsoleDSL.ConsoleProgram

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

trait AppInterpreter[F[_]] extends (AppADT ~> F) {

  def run[A](program: AppProgram[A]): F[A]

  def pure[A](a: A): F[A]

  def fromConsole[A](program: ConsoleProgram[A]): F[A]

  override def apply[A](fa: AppADT[A]): F[A] = fa match {
    case Pure(a) =>
      pure(a)
    case FromConsole(program) =>
      fromConsole(program)
  }
}

case class AsyncAppInterpreter(consoleInterpreter: ConsoleInterpreter[Future])(implicit exc: ExecutionContext) extends AppInterpreter[Future] {

  import cats.implicits._

  override def run[A](program: AppProgram[A]): Future[A] =
    program.foldMap(this)

  override def pure[A](a: A): Future[A] =
    Future.successful(a)

  override def fromConsole[A](program: ConsoleProgram[A]): Future[A] =
    program.foldMap(consoleInterpreter)

}