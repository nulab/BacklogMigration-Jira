package com.nulabinc.backlog.j2b.dsl

import cats.free.Free
import com.nulabinc.backlog.j2b.conf.AppConfiguration
import com.nulabinc.backlog.j2b.dsl.ConsoleDSL.ConsoleProgram

object AppDSL {

  type AppProgram[A] = Free[AppADT, A]

  val empty: AppProgram[Unit] =
    pure(())

  def pure[A](a: A): AppProgram[A] =
    Free.liftF(Pure(a))

  def fromConsole[A](program: ConsoleProgram[A]): AppProgram[A] =
    Free.liftF(FromConsole(program))

  def setLanguage(lang: String): AppProgram[Unit] =
    Free.liftF(SetLanguage(lang))

  def latestRelease(): AppProgram[String] =
    Free.liftF(LatestRelease)

  def exit(statusCode: Int): AppProgram[Unit] =
    Free.liftF(Exit(statusCode))

  def export(config: AppConfiguration, nextCmd: String): AppProgram[Unit] =
    Free.liftF(Export(config, nextCmd))

  def `import`(config: AppConfiguration): AppProgram[Unit] =
    Free.liftF(Import(config))

  def finalizeImport(config: AppConfiguration): AppProgram[Unit] =
    Free.liftF(FinalizeImport(config))

}
