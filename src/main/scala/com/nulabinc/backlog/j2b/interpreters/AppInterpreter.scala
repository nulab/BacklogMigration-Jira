package com.nulabinc.backlog.j2b.interpreters

import java.util.Locale

import cats.~>
import com.nulabinc.backlog.j2b.dsl.AppDSL.AppProgram
import com.nulabinc.backlog.j2b.dsl._
import com.nulabinc.backlog.j2b.dsl.ConsoleDSL.ConsoleProgram

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

trait AppInterpreter[F[_]] extends (AppADT ~> F) {

  def run[A](program: AppProgram[A]): F[A]

  def pure[A](a: A): F[A]

  def fromConsole[A](program: ConsoleProgram[A]): F[A]

  def setLanguage(lang: String): F[Unit]

  def exit(statusCode: Int): F[Unit]

  override def apply[A](fa: AppADT[A]): F[A] = fa match {
    case Pure(a) =>
      pure(a)
    case FromConsole(program) =>
      fromConsole(program)
    case SetLanguage(lang) =>
      setLanguage(lang)
    case Exit(statusCode) =>
      exit(statusCode)
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

  override def setLanguage(lang: String): Future[Unit] = Future.successful {
    lang match {
      case "ja" => Locale.setDefault(Locale.JAPAN)
      case "en" => Locale.setDefault(Locale.US)
      case _    => Locale.setDefault(Locale.getDefault)
    }
  }

  override def exit(statusCode: Int): Future[Unit] =
    sys.exit(statusCode)

}